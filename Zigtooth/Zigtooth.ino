#include <SoftwareSerial.h>
#include <stdlib.h>
#define PI 3.1415926535897932384626433832795

SoftwareSerial mySerial(10, 11); // RX, TX

const int ledPin = 13;    // Led utilizado para checar o envio e recebimento de dados
const int ledBuzzer = 8;  // Pino de controle da buzina

// Guarda a latitude e longitude do prÛpio nÛ.
String latitude_inicial = "";  
String longitude_inicial = "";

// Guarda a latitude e longitude do nÛ vizinho
String latitude = "";
String longitude = "";

// Valor float da latitude e longitude do prÛprio nÛ.
float x1 = 0;
float y1 = 0;

// Valor float da latitude e longitude nÛ vizinho.
float x2 = 0;
float y2 = 0;

// Vari·veis utilizadas na formula que calcula a dist‚ncia
float dx = 0;
float dy = 0;
float resultado;
float a;
float b;
float r;

// Flags de controle do protocolo para o prÛprio nÛ.
int la_inicial = 0;
int lon_inicial = 0;
int fim_inicial = 0;

// Flags de controle do protocolo para o nÛ vizinho.
int la = 0;
int lon = 0;
int fim = 0;

// Vari·vel utilizada para converter o valor do resultado de Float para String
char resultado_texto[20];

void setup()
{
  Serial.begin(9600);
  mySerial.begin(9600);
  
  // Configura os pinos do Led e da Buzina como saÌda
  pinMode(ledPin, OUTPUT); 
  pinMode(ledBuzzer, OUTPUT);  
  
  // Constante utilizada na formula da dist‚ncia
  r = 6371.0;
}

void loop()
{
  
  // Recebe sua prÛpria coordenada, caracter por caracter, via bluetooth do dispositivo mÛvel
  if ( (mySerial.available()) && (x1 == 0) ){
    
    // Le o caracter vindo do bluetooth
    char c_inicial = mySerial.read();
     
    // Auxilia na motagem das coordenadas
    if (c_inicial == 'X') {
      
      latitude_inicial = "";
      la_inicial = 1;  
    
    } else if ((c_inicial == 'Y') && (la_inicial == 1)) {
      
      longitude_inicial = "";
      lon_inicial = 1;
      la_inicial = 0;  
    
    } else if ((c_inicial == 'F') && (lon_inicial == 1) && (la_inicial == 0)){
      
      // Converte a latitude para float
      char buf1_inicial[latitude_inicial.length()];
      latitude_inicial.toCharArray(buf1_inicial,latitude_inicial.length());
      x1 = atof(buf1_inicial);
    
      // Converte a longitude para float
      char buf2_inicial[longitude_inicial.length()];
      longitude_inicial.toCharArray(buf2_inicial,longitude_inicial.length());
      y1 = atof(buf2_inicial);       
      
      Serial.println("X"+latitude_inicial+"Y"+longitude_inicial+"F");
      
      digitalWrite(ledPin, HIGH);
      
    }  
   
    // Monta a coordenada vinda do bluetooth
    if ((la_inicial == 1) && (c_inicial != 'X') && (x1 == 0)) {
      
      latitude_inicial = latitude_inicial + c_inicial;
      
    } else if ((lon_inicial == 1) && (c_inicial != 'Y') && (x1 == 0)) {
     
      longitude_inicial = longitude_inicial + c_inicial; 
    }

  }    
  
  // Recebe a coordenada de outro nÛ da rede e repete o precedimento de envio e montagem descrito anteriormente
  if ( (Serial.available()) && (x2 == 0) ) {
       
    char c = Serial.read();
      
    if (c == 'X') {
      
      la = 1;
      latitude = "";  
    
    } else if ((c == 'Y') && (la == 1)) {
      
      lon = 1;
      longitude = "";
      la = 0;  
    
    } else if ((c == 'F') && (lon == 1) && (la == 0)) {
      
      char buf1[latitude.length()];
      latitude.toCharArray(buf1,latitude.length());
      x2 = atof(buf1);
  
      char buf2[longitude.length()];
      longitude.toCharArray(buf2,longitude.length());
      y2 = atof(buf2);      
      
      digitalWrite(ledPin, LOW);
                        
    }
    
    // Monta a coordenada vinda do xbee   
    if ((la == 1) && (c != 'X') && (x2 == 0)) {
      
      latitude = latitude + c;
      
    } else if ((lon == 1) && (c != 'Y') && (x2 == 0)) {
     
      longitude = longitude + c; 
    }   
       
  }
  
  // Depois de montado as coordenadas do proprio nÛ e do nÛ vizinho
  // calcula-se a distancia entre eles e envia a informaÁ„o para o android via bluetooth
  if ((x1 != 0) && (x2 != 0)) {

    x1 = x1 * PI / 180.0;
    y1 = y1 * PI / 180.0;       
    
    x2 = x2 * PI / 180.0;
    y2 = y2 * PI / 180.0;
    
    dx = x2 - x1;
    dy = y2 - y1;
    
    a = sin(dx / 2) * sin(dx / 2) + cos(x1) * cos(x2) * sin(dy / 2) * sin(dy / 2);
    b = 2 * atan2(sqrt(a), sqrt((1.0 - a)));
    
    resultado = round(r * b * 1000); // Resultado em metros.
   
    // Liga a Buzina quando a dist‚ncia entre os nÛs for menor que 6 metros
    if (resultado < 6) {
      digitalWrite(ledBuzzer, HIGH);
    } else {
      digitalWrite(ledBuzzer, LOW);
    }
 
    // Envia a coordena por bluetooth
    mySerial.println("X"+latitude+"Y"+longitude+"C"+dtostrf(resultado,6,2,resultado_texto)+"F");   
    
    // Reinicia as variaveis de controle
    x1 = 0;
    x2 = 0;
    y1 = 0;
    y2 = 0;

    latitude = "";
    longitude = "";
    latitude_inicial = "";
    longitude_inicial = "";
           
    lon = 0;
    lon_inicial = 0;
    la = 0;
    la_inicial = 0;    
    
  }

}


