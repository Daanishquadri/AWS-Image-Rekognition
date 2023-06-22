# AWS-Image-Rekognition

Objective: This individual assignment aims to provide hands-on experience with the Amazon AWS cloud platform and the development of an AWS application utilizing various cloud services. The main focus is on understanding and implementing the following aspects:

1) Creation of virtual machines (EC2 instances) within the cloud.
2) Integration of cloud storage (S3) into the application.
3) Establishment of communication between VMs using a queue service (SQS).
4) Development of distributed applications using Java on Linux-based VMs in the cloud.
5)Utilization of a machine learning service (AWS Rekognition) for image recognition tasks.

Description: The task entails constructing an image recognition pipeline within AWS, employing two EC2 instances, S3 for storage, SQS for inter-VM communication, and Rekognition for machine learning capabilities. The implementation should be carried out using Java on Amazon Linux VMs. For further details, please refer to the provided diagram:

<img width="839" alt="Screenshot 2023-06-21 at 11 11 38 PM" src="https://github.com/Daanishquadri/AWS-Image-Rekognition/assets/84735952/4bb87aa3-96e0-4f5e-b205-7c9f2fe761ed">

Your have to create 2 EC2 instances (EC2 A and B in the figure), with Amazon Linux AMI, that will work in parallel. Each instance will run a Java application. Instance A will read 10 images from an S3 bucket that we created (https://njit-cs-643.s3.us-east-1.amazonaws.com) and perform object detection in the images. When a car is detected using Rekognition, with confidence higher than 90%, the index of that image (e.g., 2.jpg) is stored in SQS. Instance B reads indexes of images from SQS as soon as these indexes become available in the queue, and performs text recognition on these images (i.e., downloads them from S3 one by one and uses Rekognition for text recognition). Note that the two instances work in parallel: for example, instance A is processing image 3, while instance B is processing image 1 that was recognized as a car by instance A. When instance A terminates its image processing, it adds index -1 to the queue to signal to instance B that no more indexes will come. When instance B finishes, it prints to a file, in its associated EBS, the indexes of the images that have both cars and text, and also prints the actual text in each image next to its index.

# Attempted Solution

1) Access the login page provided by the professor in the Canvas forum's Discussion section.
2) If you don't have a student account, you can proceed to create one.
3) After creating an account and logging in to the AWS labs Course, navigate to the "modules" section where you will find the learners lab. Access it by clicking on the corresponding link.
4) Initiate the lab by clicking on the "Start Lab" button. Follow the instructions provided in the Readme tab, which can be found on the top-right section along with other buttons such as Start Lab, End Lab, AWS Details, and Reset.
5) To access the AWS console, ensure that you click on the AWS button represented by a green circle. A red circle indicates that the lab is inactive, while a green circle signifies an active lab.
6) It is crucial to copy the AWS access key, secret key, and session token, which are available in the AWS details section.
7) Additionally, download the PEM file from the SSH key section. This file will be used for authentication when accessing the EC2 instances through the terminal.
8) Proceed to the AWS Management console and search for "EC2". Upon accessing the EC2 Dashboard, follow the specified steps to create two instances.

![Screenshot 2023-06-21 at 11 52 08 PM](https://github.com/Daanishquadri/AWS-Image-Rekognition/assets/84735952/9499432e-457d-4617-b77d-f8eab664afac)
![Screenshot 2023-06-21 at 11 51 25 PM](https://github.com/Daanishquadri/AWS-Image-Rekognition/assets/84735952/4880c39b-eb7e-47dc-b45a-ca28d46fd5ea)
![Screenshot 2023-06-21 at 11 50 18 PM](https://github.com/Daanishquadri/AWS-Image-Rekognition/assets/84735952/7f1dd7be-e757-4882-b7de-df1b4ea0d299)
![Screenshot 2023-06-21 at 11 50 03 PM](https://github.com/Daanishquadri/AWS-Image-Rekognition/assets/84735952/ccbfd378-4ebb-45cb-af40-56ec8c7502c2)

# Created two EC2 instances

1) Click on the "Launch instance" button.
2) Specify the desired name for the EC2 instance you wish to create.
3) From the list of available AMIs, choose "Amazon Linux 2 AMI (HVM) - Kernel 5.10, SSD Volume Type".
4) Select the instance type as "t2.micro". T2 instances are cost-effective and provide a baseline level of CPU performance, with the ability to burst above the baseline when required.
5) The "vockey" Key-Pair value should already be pre-populated. Select it.
6) In the Network Settings section, choose to create a new security group and configure it as follows:
      Allow SSH traffic from the internet.
      Allow HTTPS traffic from the internet.
      Allow HTTP traffic from the internet.
      Instead of allowing access from "Anywhere," select "My IP" to restrict traffic to your own IP address.
7) It is generally recommended to leave the settings under "Configure storage" and "Advanced details" unchanged unless you have specific requirements or advanced knowledge.

Please note that you need to repeat the above steps twice to create two instances. The instances in this example have been named "EC2-car" and "EC2-text," but you can assign your desired names to them

![Screenshot 2023-06-21 at 11 27 11 PM](https://github.com/Daanishquadri/AWS-Image-Rekognition/assets/84735952/ef4d479c-d9e0-4bd4-a2f6-b6f841dae9d7)


# Adding IAM roles to the created AWS Instances

1) After creating your instances, navigate to the EC2 instances section to view them.
2) In case your instances are not currently running, select the desired instance, open the Instance state dropdown, and choose the "Start instance" option.
3) Once the instances are running, individually select each instance, go to the "Actions" menu, and navigate to "Security" and then "Modify IAM role."
4) From the provided dropdown list, select the pre-populated "LabInstanceProfile" as the IAM role.
5) It's important to note that this role may not have the necessary access to S3, SQS, or Rekognition, which are required for the project. Therefore, please update the IAM role accordingly.
6) If the assigned IAM role lacks access to S3, SQS, or Rekognition, proceed to the IAM section and access "Roles" to locate the "LabRole" entry.
   Assign the following permissions as policies:
      AmazonSQSFullAccess
      AmazonS3FullAccess
      AmazonRekognitionFullAccess
7) Once the policies are successfully attached, you are ready to access the EC2 instances.

![Screenshot 2023-06-21 at 11 40 44 PM](https://github.com/Daanishquadri/AWS-Image-Rekognition/assets/84735952/91aa686b-5f19-4748-9d55-035b65322d60)

![Screenshot 2023-06-21 at 11 41 39 PM](https://github.com/Daanishquadri/AWS-Image-Rekognition/assets/84735952/a5d9aba1-d430-4462-9358-162fe734538d)


# Working with Java Programs

1) We will create two distinct Java codes.
2) The first code is designed for object recognition.
3) The second code focuses on text recognition.
4) The object detection code will run on the initial instance, EC2-Car.
5) The text detection code will run on the second instance, EC2-Text.
6) I developed my programs using Eclipse and Xcode IDE.
7) Once I had the files prepared for uploading, I attempted to transfer them to the AWS instances using FileZilla. However, I encountered difficulties even after employing the following techniques:
Host: <EC2 Instance IP or hostname>
Port: 22
Protocol: SFTP - SSH File Transfer Protocol
Logon Type: Key file
User: <EC2 instance username>
Key file: <Path to your private key file (.pem)>
8) I also attempted to use the "scp" command to upload the code to the instances, but encountered errors that prevented successful transfer.

# SSH Access from a MAC

1) We utilized SSH to access both instances by using their respective IP addresses.
2) Here are the steps I followed to access both instances via the terminal.
3) Please note that these instructions are intended for Mac/Linux users only.
4) Prior to starting the actions, carefully read through the following two bullet points. These instructions will not be visible once the AWS Details panel is open.
5) Click on the "AWS Details" link provided above these instructions.
6) Download the "labsuser.pem" file by clicking the "Download PEM" button. Save the file, typically in the Downloads directory.
7) Open a terminal window and navigate to the directory where the .pem file was downloaded. Use the "cd" command to change the directory. For example, if the file was saved in the Downloads directory, run the following command:
cd ~/Downloads
8) Change the permissions of the key file to be read-only by executing the following command:
chmod 400 labsuser.pem
9) Return to the AWS Management Console and access the EC2 service. Choose the "Instances" option.
10) In the "Description" tab, select the checkbox next to the instance you wish to connect to.
11) Copy the IPv4 Public IP value from the instance details. This will be the IP address of your instance.
12) Go back to the terminal window and run the following command (replace <filename> with the actual filename of the .pem file and <public-ip> with the copied IP address):
ssh -i <filename>.pem ec2-user@<public-ip>
13) When prompted to allow a first connection to this remote SSH server, type "yes".
Since you are using a key pair for authentication, you will not be prompted for a password.


![Screenshot 2023-06-22 at 12 14 30 AM](https://github.com/Daanishquadri/AWS-Image-Rekognition/assets/84735952/86f2c537-14ec-4fd0-8c54-4238ddaff1ee)
![Screenshot 2023-06-22 at 12 14 19 AM](https://github.com/Daanishquadri/AWS-Image-Rekognition/assets/84735952/2b790ca1-01d6-4635-a9af-a9d33bdcf41e)
![Screenshot 2023-06-21 at 11 27 11 PM](https://github.com/Daanishquadri/AWS-Image-Rekognition/assets/84735952/97cea698-6e49-44dd-856a-9be70465ad94)
![Screenshot 2023-06-20 at 10 43 03 PM](https://github.com/Daanishquadri/AWS-Image-Rekognition/assets/84735952/91a030ce-97f6-4853-b738-437bd8186070)



# Running the programs on EC2 instances

1) Make sure to update your access_key, secret_key, and default_region on your AWS terminal.
To update the above information, type aws configure on the AWS terminal.
The default region should be us-east-1.
2) Once configuration is done we will be running the programs by running the commands javac and the name of the particular file you are trying to run.
3) For the second program we will be needing the output the result would be in a text file so we will run javac the particular file name output.txt.
4) In my case I tried to run my codes and it was giving me errors so I could not go any further attaching the screenshots below.

![Screenshot 2023-06-22 at 12 26 41 AM](https://github.com/Daanishquadri/AWS-Image-Rekognition/assets/84735952/666c194f-7e3d-4379-ad7a-4d7ba1584f0f)
<img width="254" alt="Screenshot 2023-06-22 at 12 35 10 AM" src="https://github.com/Daanishquadri/AWS-Image-Rekognition/assets/84735952/81340853-af25-4150-9c59-c1b3559b9c44">


   






