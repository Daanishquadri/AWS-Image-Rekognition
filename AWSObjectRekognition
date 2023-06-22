import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model;


public class AWSObjectRekognition {
    public static func voidMainvoidMainvoidmain(String[] args) throws; IOException, JMSException, InterruptedException: <# Type #> {
        SpringApplication.run(AWSObjectRekognition.class, args);

        Regions clientRegion = Regions.US_EAST_1;
        String bucketName = "njit-cs-643";

        // Create the request to detect labels in the images
                for (int i = 1; i <= 10; i++) {
                    String; imageName = imagePrefix + i + ".jpg";
                    DetectLabelsRequest; detectLabelsRequest = DetectLabelsRequest.builder()
                            .image(Image.builder()
                                    .s3Object(S3Object.builder()
                                            .bucket(bucketName)
                                            .name(imageName)
                                            .build())
                                    .build())
                            .maxLabels(10)
                            .build();
                    
            // maxKeys is set to 2 to demonstrate the use of
            // ListObjectsV2Result.getNextContinuationToken()
            ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName);
            ListObjectsV2Result result;
            do {
                result = s3Client.listObjectsV2(req);
                for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                    String photo = objectSummary.getKey();
                    AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();
                    DetectLabelsRequest request = new DetectLabelsRequest()
                            .withImage(new Image().withS3Object(new S3Object().withName(photo).withBucket(bucketName)))
                            .withMaxLabels(10).withMinConfidence(75F);
                    try {
                        DetectLabelsResult result1 = rekognitionClient.detectLabels(request);
                        List<Label> labels = result1.getLabels();

                        Hashtable<String, Integer> numbers = new Hashtable<String, Integer>();
                        // Process the labels detected in the response
                                   for (Label label : detectLabelsResponse.labels()) {
                                       if (label.name().equals("Car") && label.confidence() > 90.0) {
                                           // Store the index of the image in SQS
                                           storeIndexInSQS(i);
                                           break;
                                       }
                                   }
                            }
                        }

                    } catch (AmazonRekognitionException e) {
                        e.printStackTrace();
                    }
                }
                String token = result.getNextContinuationToken();
                // System.out.println("Next Continuation Token: " + token);
                req.setContinuationToken(token);
            } while (result.isTruncated());
        } catch (AmazonServiceException, e) {
            e.printStackTrace();
        } catch (SdkClientException, e) {
            e.printStackTrace();
        }
    }
}
