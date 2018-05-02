/*
 * Changing colors with epotentiometer */

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
  
   
}

void loop()
{
  potValue = analogRead(potPin_red);
  photoValue = analogRead(photoPin);
  //Serial.println(readValue_red);

  
 // writeValue_red = one_step_c_value*readValue_red; //Calculate the value to write on the red LED (add point to change to float point)
  //writeValue_green = one_step_c_value*readValue_green; //Calculate the value to write on the green LED
  //writeValue_blue = one_step_c_value*readValue_blue; ///Calculate the value to write on the blue LED

  //writeValue_red =  map(readValue_red, 0, 4092, 0, 255);
  //writeValue_green =  map(readValue_green, 0, 4092, 0, 255);
  //writeValue_blue =  map(readValue_blue, 0, 4092, 0, 255);

   // Serial.println(potValue);
 if (potValue < 1364)  // Lowest third of the potentiometer's range (0-340)
  {                  
    potValue =  map(potValue, 0, 1363, 0, 255);

    writeValue_red = 256 - potValue;  // Red from full to off
    writeValue_green = potValue;        // Green from off to full
    writeValue_blue = 1;             // Blue off
  }
  else if (potValue < 2728) // Middle third of potentiometer's range (341-681)
  {
     potValue =  map(potValue, 1364, 2727, 0, 255); // Normalize to 0-255

    writeValue_red = 1;            // Red off
    writeValue_green = 256 - potValue; // Green from full to off
    writeValue_blue = potValue;       // Blue from off to full
  }
  else  // Upper third of potentiometer"s range (682-1023)
  {
    potValue =  map(potValue, 2728, 4092, 0, 255); // Normalize to 0-255

     writeValue_red =potValue ;       // Red from off to full
    writeValue_green = 1;            // Green off
    writeValue_blue = 256 - potValue; // Blue from full to off
  }

 Serial.print(photoValue);
    Serial.print(",,");
    Serial.println(potValue);
     // Serial.print(writeValue_red);
     // Serial.print(",");
     // Serial.print(writeValue_blue);
     // Serial.print(",");
     // Serial.println(writeValue_green);

  
  analogWrite(redPin,writeValue_red);
  analogWrite(greenPin,writeValue_green);
  analogWrite(bluePin,writeValue_blue);

  delay(100);
}

