package com.asusoftware.Gym_Management_BE.stripe.service;

import com.asusoftware.Gym_Management_BE.user.model.User;
import com.asusoftware.Gym_Management_BE.user.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.net.Webhook;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.SubscriptionCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;


@Service
public class StripeService {

    private static final Logger logger = Logger.getLogger(StripeService.class.getName());

    @Value("${stripe.secret.key}")
    private String secretKey;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @Value("${stripe.success.url}")
    private String successUrl;

    @Value("${stripe.cancel.url}")
    private String cancelUrl;

    @Autowired
    private UserRepository userRepository;

    /**
     * Inițializează cheia API Stripe la pornirea aplicației.
     */
    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    /**
     * Creează un client Stripe asociat unui utilizator.
     * @param user Utilizatorul pentru care se creează clientul
     * @return ID-ul clientului Stripe
     * @throws StripeException În caz de eroare Stripe
     */
    public String createCustomer(User user) throws StripeException {
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setEmail(user.getEmail())
                .setName(user.getFirstName() + " " + user.getLastName())
                .build();

        Customer customer = Customer.create(params);
        user.setStripeCustomerId(customer.getId()); // Salvează ID-ul Stripe al utilizatorului
        userRepository.save(user);
        return customer.getId();
    }

    /**
     * Creează un abonament Stripe pentru un utilizator existent.
     * @param customerId ID-ul clientului Stripe
     * @param priceId ID-ul prețului abonamentului
     * @return Detaliile abonamentului Stripe
     * @throws StripeException În caz de eroare Stripe
     */
    public Subscription createSubscription(String customerId, String priceId) throws StripeException {
        SubscriptionCreateParams params = SubscriptionCreateParams.builder()
                .setCustomer(customerId)
                .addItem(SubscriptionCreateParams.Item.builder().setPrice(priceId).build())
                .setPaymentBehavior(SubscriptionCreateParams.PaymentBehavior.DEFAULT_INCOMPLETE)
                .setCancelAtPeriodEnd(false)
                .build();

        return Subscription.create(params);
    }

    /**
     * Creează un PaymentIntent pentru o plată unică (ex: acces de o zi).
     * @param amount Suma plății (în unități mici, ex: cenți)
     * @param currency Moneda (ex: USD, EUR)
     * @param customerId ID-ul clientului Stripe
     * @return PaymentIntent generat
     * @throws StripeException În caz de eroare Stripe
     */
    public PaymentIntent createPaymentIntent(Long amount, String currency, String customerId) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount * 100) // Stripe acceptă sumele în cenți
                .setCurrency(currency)
                .setCustomer(customerId)
                .build();

        return PaymentIntent.create(params);
    }

    /**
     * Procesează webhook-urile Stripe pentru evenimente precum plăți reușite/eșuate.
     * @param payload Corpul JSON al webhook-ului
     * @param sigHeader Semnătura Stripe pentru validare
     */
    public void processWebhook(String payload, String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            switch (event.getType()) {
                case "invoice.payment_succeeded":
                    Invoice invoice = (Invoice) event.getDataObjectDeserializer().getObject().orElse(null);
                    if (invoice != null) {
                        handleSuccessfulPayment(invoice);
                    }
                    break;
                case "invoice.payment_failed":
                    handleFailedPayment(event);
                    break;
                case "customer.subscription.updated":
                    Subscription subscription = (Subscription) event.getDataObjectDeserializer().getObject().orElse(null);
                    if (subscription != null) {
                        handleSubscriptionUpdate(subscription);
                    }
                    break;
                case "customer.subscription.deleted":
                    handleSubscriptionCancellation(event);
                    break;
                default:
                    logger.info("Unhandled event type: " + event.getType());
            }
        } catch (Exception e) {
            logger.severe("Error processing Stripe webhook: " + e.getMessage());
        }
    }

    /**
     * Tratează o plată reușită pentru abonamente.
     */
    private void handleSuccessfulPayment(Invoice invoice) {
        logger.info("Payment succeeded for invoice: " + invoice.getId());
        // Aici putem marca abonamentul utilizatorului ca activ
    }

    /**
     * Tratează o plată eșuată.
     */
    private void handleFailedPayment(Event event) {
        logger.warning("Payment failed for event: " + event.getId());
        // Aici putem trimite notificări utilizatorului
    }

    /**
     * Tratează actualizările abonamentului (ex: modificare de plan).
     */
    private void handleSubscriptionUpdate(Subscription subscription) {
        logger.info("Subscription updated: " + subscription.getId());
        // Putem actualiza informațiile despre abonament în baza de date
    }

    /**
     * Tratează anularea unui abonament.
     */
    private void handleSubscriptionCancellation(Event event) {
        logger.warning("Subscription cancelled: " + event.getId());
        // Putem dezactiva accesul utilizatorului la sala de sport
    }
}
