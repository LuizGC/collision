collision
=========

Sistema embarcado para alerta de colisão entre veículos Vehicular Ad Hoc Networks (VANETs) são redes ad hoc nas quais os nós são veículos automotores que apresentam alta mobilidade e podem se comunicar uns com os outros (V2V - Vehicle-to-Vehicle) ou com uma infra-estrutura (V2I - Vehicle-to- infrastrucuture). Devido ao grande número de acidentes, as principais aplicações desenvolvidas para VANETs são as voltadas a segurança. Neste contexto, o conhecimento da posição geográfica de um veículo em relação aos seus vizinhos é de grande importância para auxiliar o motorista em suas decisões. O objetivo desse trabalho é desenvolver um sistema embarcado para alerta de colisão entre veículos que utilize um buzzer para alertar os motoristas em prováveis situações de colisão entre veículos. 
O hardware utilizado: 
- Arduino Uno R3; o com cristal oscilador de 16 MHz e o Circuito de Reset;
- Bluetooth : JY-MCU Arduino Bluetooth Wireless Serial Port Module; 
- Xbee Pro S2B; Celular com Android 2.3.3 e GPS; 
- Buzzer: Buzina para disparar o alerta de colisão. 

Funcionamento: As coordenadas do veículo serão adquiridas através do GPS contido no celular com Android. Essas coordenadas serão enviadas via Bluetooth para o dispositivo embarcado. No dispositivo embarcado, as coordenadas recebidas pelo bluetooth serão transmitidas via Broadcast através do xbee para todos os dispositivos em seu raio de alcance. O dispositivo embarcado irá também receber coordenadas via xbee vindas de outros veículos com o mesmo dispositivo. O sistema irá disparar um sinal sonoro, através de um Buzzer, para alertar o motorista quando um veículo estiver muito próximo do seu. Como o Arduino Uno R3 só possui um RX e um TX teremos que emular por software mais um RX e TX através de outras portas do arduino. Com isso, será possível o funcionamento simultâneo do Bluetooth e Xbee.
