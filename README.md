<<<<<<< HEAD
# Human Activity Recognition
<br />
INFO 7245- Big-Data-Systems-and-Intelligence-Analytics <br />
Final Project <br />
<br />
<b>Problem Statement</b>: Predict Human Activity and classify them into WALKING, WALKING UPSTAIRS, WALKING DOWNSTAIRS, SITTING, STANDING and LAYING </br  >
</br  >
<b>Dataset</b>: Human Activity Recognition </br  >
7352 observations with 128 datapoints taken within 2.56 seconds with 50% carry forwarded in the training dataset. Each datapoint has 9 measurements for:</br  >
•	Body Acceleration X axis </br  >
•	Body Acceleration Y axis </br  >
•	Body Acceleration Z axis </br  >
•	Body Gyroscope X axis </br  >
•	Body Gyroscope Y axis </br  >
•	Body Gyroscope Z axis </br  >
•	Total Acceleration X axis </br  >
•	Total Acceleration Y axis </br  >
•	Total Acceleration Z axis </br  >
 </br  >
<b>Goal</b>: </br  >
•	Showcase that Standard Neural Networks shouldn’t be used to predict Sequential Classification Problem </br  >
•	Implement Deep Neural Networks and prove it to be better than Standard Neural Network for Sequential Classification Problem </br  >
 </br  >
<b>Steps Completed</b>: </br  >
•	Importing data </br  >
•	Exploratory Data Analysis </br  >
•	Implementing Machine Learning algorithms </br  >
 >o	Decision Tree </br  >
 >o	K Nearest Neighbors </br  >
 >o	SVC </br  >
 >o	Gaussian Naïve Bayes </br  >
 >o	Quadratic Discriminant Analysis </br  >
•	Implementing Recurrent Neural Network (RNN) with keras</br  >
•	Implementing Long-Short Term Memory (LSTM) with tensorflow</br  >
•	Created Android App to track Human Activity using Accelerometer and Gyroscope sensors</br  >
 </br  >
<b>Results</b>: </br  >
LSTM has a better accuracy at predicting the human activity as compared to any machine learning algotihm or even RNN. RNN has 81.81% accuracy where as LSTM has 88.97% accuracy</br  >
 </br  >
<b>Future Scope</b>: </br  >
•	Inclusion of third variable to improve prediction accuracy </br  >
•	Android App enhancements with user better interface </br  >
•	Implement flask API to display output on a remote server </br  >
=======
# Human-Activity-Recognition
<br />
This is a sequential classification problem which I am trying to solve by predicting human activity, given their accelerometer  and gyroscope readings.<br />
Human activity recognotion is a vintage concept which has gained importance over the years. Border security, athletes and general surveillance being some of its applications, I decided to implement this project to get a deeper understanding of deep neural networks. My goals from this project was to prove that standard neural networks fail to accurately predict data when its previous state or history of the object is taken into consideration and also to prove that deep neural networks works best in this scenario.<br />
<br />
Having completed the first goal, I am currently in the process of improving the accuracy of Recurrent Neural Network. I have also finalized an Android simulator which can give me accelorometer and gyroscope readings which will act as live imputs to my algorithm. Having obtained satisfactory results from this, I plan to develope an Android app and integrate my model into it.<br />
<br />
For detailed description about each step, please refer the Progress Report.<br />
<br />
Stay tuned for more updates :)
>>>>>>> 5f466373f11557a08bca4c44be248b00e949a00f
