import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

class MyListener implements MessageListener {

    @Override
    public void onMessage(Message message) {

        try {
            Regions clientRegion = Regions.US_EAST_1;
            String bucketName = "njit-cs-643";

            RekognitionClient rekognitionClient = RekognitionClient.builder()
                    .region(region)
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
            
            // Continuously monitor SQS for image indexes
            while (true) {
                int index = getIndexFromSQS();
                
                // Exit the loop if index is -1 (end of processing)
                if (index == -1) {
                    break;
                }
                
            result = s3Client.listObjectsV2(req);
            for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                String m = (String) ((TextMessage) message).getText().toString();
                if (objectSummary.getKey().contains(m)) {
                    // System.out.println("Received: " + ((TextMessage) message).getText());
                    String photo = objectSummary.getKey();
                    // text rekognition of the image from the queue
                    DetectTextRequest request = new DetectTextRequest()
                            .withImage(new Image()
                                    .withS3Object(new S3Object()
                                            .withName(photo)
                                            .withBucket(bucketName)));
                    try {
                        DetectTextResult result1 = rekognitionClient.detectText(request);
                        List<TextDetection> textDetections = result1.getTextDetections();
                        if (!textDetections.isEmpty()) {
                            System.out.print("Text Detected lines and words for:  " + photo + " ==> ");
                            for (TextDetection text : textDetections) {

                                System.out.print("  Text Detected: " + text.getDetectedText() + " , Confidence: "
                                        + text.getConfidence().toString());
                                System.out.println();
                            }
                        }
                    } catch (AmazonRekognitionException e) {
                        System.out.print("Error");
                        e.printStackTrace();
                    }
                }
            }

         catch (JMSException e) {
            System.out.println("Please run the Instance-1 first...");
        }
}

public class AWSTextRekognition {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(AWSTextRekognition.class, args);

        Regions clientRegion = Regions.US_EAST_1;

        try {
            AmazonSQSClientBuilder.standard()
                    .withRegion(clientRegion)
                    .build();

            // creating SQS queue even if it is not created it will wait for instance 2 to
            // start first.
            try {
                // Create a new connection factory with all defaults (credentials and region)
                // set automatically
                SQSConnectionFactory connectionFactory = new SQSConnectionFactory(new ProviderConfiguration(),
                        AmazonSQSClientBuilder.defaultClient());

                // Create the connection.
                SQSConnection connection = connectionFactory.createConnection();
                // Get the wrapped client
                AmazonSQSMessagingClientWrapper client = connection.getWrappedAmazonSQSClient();

                if (!client.queueExists("MyQueue.fifo")) {
                    Map<String, String> attributes = new HashMap<String, String>();
                    attributes.put("FifoQueue", "true");
                    attributes.put("ContentBasedDeduplication", "true");
                    client.createQueue(
                            new CreateQueueRequest().withQueueName("MyQueue.fifo").withAttributes(attributes));
                }

               

            }catch (Exception e) {
                System.out.println("Please run the Instance-1, the program will wait for the queue to have elements.");
                SQSConnectionFactory connectionFactory = new SQSConnectionFactory(new ProviderConfiguration(),
                        AmazonSQSClientBuilder.defaultClient());

               
                }
            }

        } 
    }
}