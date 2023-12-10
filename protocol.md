# Communication protocol

This document describes the protocol used for communication between the different nodes of the
distributed application.

## Terminology

* Sensor - a device which senses the environment and describes it with a value (an integer value in
  the context of this project). Examples: temperature sensor, humidity sensor.
* Actuator - a device which can influence the environment. Examples: a fan, a window opener/closer,
  door opener/closer, heater.
* Sensor and actuator node - a computer which has direct access to a set of sensors, a set of
  actuators and is connected to the Internet.
* Control-panel node - a device connected to the Internet which visualizes status of sensor and
  actuator nodes and sends control commands to them.
* Graphical User Interface (GUI) - A graphical interface where users of the system can interact with
  it.

## The underlying transport protocol

TODO - what transport-layer protocol do you use? TCP? UDP? What port number(s)? Why did you
choose this transport layer protocol?

In the development of the client-server network application, the Transmission Control Protocol (TCP) we choose the transport layer protocol. This choice is based on TCP's characteristics which are well-suited for applications that require reliable, ordered, and error-checked delivery of streams of data. The port numbers for this application are variable, determined at runtime.

Protocol Selection: TCP vs UDP
Transmission Control Protocol (TCP)

Reliability: TCP provides a reliable communication channel, ensuring that all data sent by the client is received by the server, and the same goes for the other way around, without corruption or loss. This is essential for the integrity of the data exchanged in our application.

Flow Control: TCP manages the rate of data transmission based on network capacity, preventing network congestion and ensuring efficient data transfer.
Connection-Oriented Nature: TCP establishes a connection before data transmission and maintains it until the communication is complete. This persistent connection is necessary for ongoing interactions between the client and server in our application.
User Datagram Protocol (UDP)
While UDP offers faster data transmission rates by eliminating overheads (like handshaking, error recovery), it does not guarantee data delivery, order, or integrity. This makes UDP less suitable where data reliability is a priority.
We did however almost complete a branch of our solution with both TCP and UDP implementation. Sadly we did not have enough time to merge it into the main branch.

Port Number Selection
Dynamic Assignment: The port numbers for the application are not hardcoded but are assigned dynamically. This approach offers flexibility, allowing the application to operate on different network environments without conflicts with other services.

Conclusion
The decision to use TCP as the transport layer protocol is driven by the need for reliable, ordered, and error-checked communication between the client and server. The flexible approach to port number selection further ensures that the application can adapt to various network configurations.

## The architecture

TODO - show the general architecture of your network. Which part is a server? Who are clients?
Do you have one or several servers? Perhaps include a picture here.

The system is built on a singular access point between clients and servers. The general system
bases itself on a one-to-many relationship from server to clients. The server itself is responsible
for routing and broadcasting, as well as managing clients's connections. Clients behave
independent of the server after establishing a connection.

## The flow of information and events

TODO - describe what each network node does and when. Some periodic events? Some reaction on
incoming packets? Perhaps split into several subsections, where each subsection describes one
node type (For example: one subsection for sensor/actuator nodes, one for control panel nodes).

The server starts as without any connections to any clients.
- When any client connects, the server establishes an independent connection to the client and then
gives it a unique identifier, which it then informs the client about. The client makes note of its
own identifier.
- When a node connects for the first time, it will broadcast its available actuators and its current
sensor readings
- When a control panel connects for the first time, it will broadcast an info request to all nodes
currently connected to the network. All nodes that get the message will then route their information
back to the sender.

When a message is sent to the server, the server establishes whether it's intended for a singular
recipient, a set of recipients (multicast), all the connected clients (broadcast), or directly to
the server.

The server runs in full duplex, with each client having their own thread to receive messages and
the server dedicating independent threads to receive messages from each client.

## Connection and state

TODO - is your communication protocol connection-oriented or connection-less? Is it stateful or
stateless?
Our solution is Connection-Oriented because:
The use of TCP  sockets is a clear indication of a connection-oriented approach. TCP, unlike UDP , requires the establishment of a connection before data is transmitted.

In our code, both the client (Client) and server (Server) utilize these TCP sockets to establish communication. 
The client creates a socket to connect to the server, and the server listens for incoming connections on a ServerSocket and then accepts them.

Dedicated Connection Management:
The server class manages each client connection individually. Upon accepting a new client connection, it creates a new instance of ServerConnection, dedicated to handling communication with that specific client.
This is a classic example of connection-oriented protocols, where each client-server pair maintains an individual, persistent connection.

Stateful
Session Management with UUIDs:
The system assigns a unique UUID to each client session. This UUID is used to identify and manage each client throughout their session.
The maintenance of session IDs allows the server to keep track of each individual clients state, which is crucial for stateful operations.

State-Aware Interactions:
The servers capability to broadcast messages to all clients or route messages to specific clients based on their UUID demonstrates state awareness. 
This would not be possible in a stateless protocol where each message/request would be treated independently without any context of past interactions.

Handling Connection Lifecycle Events:
The protocol interface includes methods like onClientConnect, onClientDisconnect, and receiveMessage. 
These methods are designed to respond to specific events in the lifecycle of a client connection, indicating that the server is keeping track of the state of each connection.

Conclusion
In summary, the solution is connection-oriented because it establishes and maintains dedicated connections between the server and each client using TCP sockets. It is stateful as it manages and tracks the state of each client through unique session IDs and responds to state changes and events specific to each client.


## Types, constants

TODO - Do you have some specific value types you use in several messages? They you can describe
them here.

In the design of our client-server communication framework, we have implemented several specific value types and constants that are consistently used across various messages. These types are fundamental to the operation of our network protocol and ensure a standardized approach to message handling. Key types and constants:

1. Universal Unique Identifier (UUID)
Usage: Both as source and destination in the Message class.

Purpose: Provides a unique identifier for each client within the network. This is crucial for accurately routing messages between clients and the server.

Characteristics: A UUID is a 128-bit number used to identify information in computer systems. In our framework, it ensures that each client-server interaction is distinct and traceable.

2. Generic Payload (T extends Serializable)
Usage: As the payload in the Message<T> class.

Purpose: To encapsulate the data being transmitted in a message. The generic type T allows for flexibility in the type of data transmitted, as long as it conforms to Java's serialization mechanism.

Characteristics: This approach allows our communication framework to be adaptable and extendable to various data types, ranging from simple strings to complex data structures, provided they are serializable.

3. Message Class (Message<T extends Serializable>)

Components:

UUID source: Identifies the sender of the message.

UUID destination: specifies the intended recipient of the message.

T payload: Contains the actual data/message to be transmitted.

Purpose:
 Serves as a blueprint for all messages transmitted within our network, ensuring a consistent structure that includes sender, receiver, and the message content.

4. UuidMessage Class

Description: A specialized form of Message<UUID> used specifically for transmitting a UUID as the payload.

Purpose: Primarily used for sending a client's UUID from the server to the client upon establishing a connection. It plays a crucial role in client identification and session management.

Conclusion
The consistent use of these types and constants across our message communications ensures a robust and flexible system. UUIDs provide a reliable way of identifying clients and routing messages, while the generic payload design allows our system to handle a wide variety of data types. The Message and UuidMessage classes provide structured formats for all communications, facilitating clear and efficient message handling within our client-server architecture.

## Message format

TODO - describe the general format of all messages. Then describe specific format for each
message type in your protocol.

All messages sent on the network are Message objects. The Message class encapsulates a generically
typed payload, and contains identifiers for the sender and receiver of a message. Messages are not
sent directly, and instead messages should extend the Message class with a specified payload type
for integrity ensurance.

The destinations of messages can be one of four types:
- The server, indicated by a null destination.
- All connected control panels, using the control panel multicast flag
- All connected nodes, using the node multicast flag
- A specific client, using their UUID.

The protocol consists of a few message types for the different functions of the program:
- ConnectionMessage: Connection messages are sent to the server when a client first connects. The
message is intended to help the server figure out what type of client has connected in order to
assist in multicasting. The message contains only an enum as a payload, which specifies what type of
client the sender is.
-NodeDisconnectMessage: Node disconnection messages are simple messages used to inform control
panels about a node disconnecting from the server. These are typically multicasted to all control
panels.
- NodeInfoMessage: Node info messages are either multicasted to all control panels or routed to
one specific control panel. When a node first connects, it multicasts a message to all control
panels connected to the system and reveals which actuators it has and the current state of its
sensors.
- NodeInfoRequestMessage: Related to node info messages, node info request messages are a request
to nodes for them to send their node info to the original sender. When a control panel first
connects, it multicasts the request to all the nodes on the network, who then respond by routing a
node info message to the requesting control panel.
- SensorUpdateMessage: Sensor update messages are messages sent from sensors to control panels to
inform them about changes to the sensor's readings. They are typically multicasted to all control
panels.
- ActuatorCommandMessage: Actuator command messages are commands sent from a control panel to a node
requesting it to change the state of a particular actuator.
- ActuatorUpdateMessage: The info equivalent to the actuator command messages, actuators multicast
these messages to control panels to inform them of a change to an actuator's state.


### Error messages

TODO - describe the possible error messages that nodes can send in your system.

## An example scenario

TODO - describe a typical scenario. How would it look like from communication perspective? When
are connections established? Which packets are sent? How do nodes react on the packets? An
example scenario could be as follows:
1. A sensor node with ID=1 is started. It has a temperature sensor, two humidity sensors. It can
   also open a window.
2. A sensor node with ID=2 is started. It has a single temperature sensor and can control two fans
   and a heater.
3. A control panel node is started.
4. Another control panel node is started.
5. A sensor node with ID=3 is started. It has a two temperature sensors and no actuators.
6. After 5 seconds all three sensor/actuator nodes broadcast their sensor data.
7. The user of the first-control panel presses on the button "ON" for the first fan of
   sensor/actuator node with ID=2.
8. The user of the second control-panel node presses on the button "turn off all actuators".

1. The server is started.
2. A new node establishes a connection with the server. It sends a request to get a unique address.
3. The server receives the request, and sends the client's unique address to it.
4. The node receives its address, and proceeds to send out a multicast to all control panels with
information about which actuators it has available (in this case it can open a window) and which
sensors it has, along with their current readings (in this case a temperature sensor at 22 degress
celsius)
5. The server receives the multicast, but drops it since no control panels are connected.
6. A control panel establishes a connection with the server. It similarly sends a request to get a
unique address, and the server promptly provides it with one.
7. Once the control panel receives its address, it multicasts a request to all connected sensor
nodes to get their available data.
8. The server receives the request and forwards it to all connected sensor nodes, in this case only
the one from point 2.
9. The sensor node receives the request, reads off the request's sender's address, and sends a
message containing the sensor node's actuators and sensor info to that address.
10. The server receives the message, reads off the destination, and routes the message to the
control panel.
11. The control panel receives the message, reads its payload, and adds the data to the interface.
Since the message is from an unknown node, it adds a new tab for the node.
12. The user uses the control panel's interface to open a window by pressing the checkbox.
13. The control panel sends a message routed to the sensor node with which the window is associated,
which is then routed by the server similarly to the other messages.
14. The sensor node receives the message, and opens the window. It then sends a multicast to all
connected control panels notifying them of the change.
15. The sensor node notices that the temperature of the room has changed, and multicasts a sensor
info message to all control panels on the network, containing information about the sensor change.
16. The control panel receives the message and updates the data in the interface.
17. The user goes to the sensor node and shuts it down for the night in order to save power.
18. The sensor node multicasts its final words to all connected control panels, telling them that
it is leaving. The control panel picks up this message and removes it from its interface.
## Reliability and security

TODO - describe the reliability and security mechanisms your solution supports.
