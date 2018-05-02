/*
   Changing colors with epotentiometer */

SYSTEM_MODE(MANUAL);

/*
  Adafruit Arduino - Lesson 3. RGB LED
*/

int redPin = D0;
int greenPin = D1;
int bluePin = D2;


int potPin_red = A0;
int photoPin = A1;



int potValue;
int photoValue;



int writeValue_red; //declare variable to send to the red LED
int writeValue_green; //declare variable to send to the green LED
int writeValue_blue; //declare variable to send to the blue LED

#define COMMON_ANODE

void setup()
{
  pinMode(redPin, OUTPUT);
  pinMode(greenPin, OUTPUT);
  pinMode(bluePin, OUTPUT);

  pinMode(potPin_red, INPUT); //set potentiometer for red LED as input
  pinMode(photoPin, INPUT); //set potentiometer for green LED as input

  Serial.begin(9600);
  setColor(255, 0, 0 , 0);


}

void loop()
{
  potValue = analogRead(potPin_red);
  photoValue = analogRead(photoPin);

   Serial.print("pot value:");
   Serial.println(potValue);
  if (potValue < 1364)  // Lowest third of the potentiometer's range (0-340)
  {
    potValue =  map(potValue, 0, 1363, 0, 255);
    setColor(256 - potValue,  potValue, 1, photoValue);
  }
  else if (potValue < 2728) // Middle third of potentiometer's range (341-681)
  {
    potValue =  map(potValue, 1364, 2727, 0, 255); // Normalize to 0-255
    setColor(1, 256 - potValue, potValue, photoValue);

  }
  else  // Upper third of potentiometer"s range (682-1023)
  {
    potValue =  map(potValue, 2728, 4092, 0, 255); // Normalize to 0-255
    setColor(potValue, 1, 256 - potValue, photoValue);
  }


Serial.print("Original photo value");
  Serial.println(photoValue);



  delay(200);
}

void setColor(int r, int g, int b, int photoValue) {
  photoValue = map(photoValue, 2600, 3800, 0, 255);
  if(photoValue < r) {
  r = r-photoValue;
  } else {
    r = 0;
  }
  if (photoValue < g) {
    g = g-photoValue;
  } else {
    g = 0;
  }
  if(photoValue < b) {
  b = b-photoValue;
  } else {
    b = 0;
  }
 
  writeValue_red = 255 - r ;       // Red from off to full
  writeValue_green = 255 - g;            // Green off
  writeValue_blue = 255 - b;

Serial.print("writeValue_red" +  writeValue_red);
  Serial.print("newPhoto");
  Serial.println(photoValue);
    analogWrite(redPin, writeValue_red);
  analogWrite(greenPin, writeValue_green);
  analogWrite(bluePin, writeValue_blue);

  // Serial.print(writeValue_red);
  // Serial.print(",");
  // Serial.print(writeValue_blue);
  // Serial.print(",");
  // Serial.println(writeValue_green);



}





