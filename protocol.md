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

The Underlying Transport Protocol

In our communication system,we strategically utilize both Transmission Control Protocol (TCP) and User Datagram Protocol (UDP) as our transport-layer protocols. This dual-protocol approach is designed to leverage the distinct advantages of each protocol, ensuring efficient and reliable data transmission under varying network conditions.
TCP:
Implementation: Our system employs TCP for critical operations where data integrity and order are paramount. This is facilitated through the use of ServerSocket and Socket classes in the server, alongside TCPClientSocket  for the client.
Port numbers: While specific port number are configurable based on deployment needs,they are essential for establishing TCP connections between the server and the clients.

TCP gives us:
Reliability: TCP connection-oriented nature guarantees that data packet are delivered in the correct order and without loss,which is vital for transaction requiring high data integrity.
Flow Control: with built-in mechanisms for flow an congestion control,TCP ensures stable communication,preventing network congestion even in high-traffic scenarios.

UDP(User Datagram Protocol)

Implementation: For scenarios necessitating quick data transmission,our system incorporates UDP,using DatagramSocket in the server and udpClientSocket in the client.

Port numbers: As with TCp,UDP port numbes are assigned during the setup of the respective sockets,tailored to the specific needs of the application.

UDP gives us:
Efficiency and Speed: UDP connection-less protocol offers faster data transmission for time-sensitive applications or where high-speed communication is required.

Lower Overhead: with minimal protocol overhead,UDP is more efficient for small data packets and scenarios where occasional packet loss is not critical.

Conclusion:

The combinations of TCP and UDP in our communication protocol presents a versatile framework capable of handling a wide range of communication needs. TCP is leveraged for operations demanding high reliability and data integrity, while UDP is utilized for its efficiency and speed,particularly in less critical or real-time scenarios. The flexibility in choosing port numbers allows our system to be adaptable and efficient.

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

Connection- Oriented and Connection-Less characteristics
Our communication protocol uses a dual nature:
Connection-oriented (TCP):
the protocol leverages TCP (Transmission Control Protocol) for certain interactions. This is evident from the use of ServerSocket and Socket class in the server implementation and TcpClientSocket in the client implementation.

TCP,known for its reliability and sequence ordered data transmission, requires establishing a connection before any data exchange. This is reflected in our protocol method of handling TCP connections (HandleTCPConnection), which deals with the intricacies of client-server communication over TCP.

Connection-Less (UDP)
In parallel,the protocol incorporates UDP(User Datagram Protocol) functionalities. This is apparent from the utilization of DatagramSocket in the server and and UdpClientSocket in the client.

UDP is a connection less protocol,characterized by sending data in discrete packets without establishing a persistent connection. The method handleUdpPacket in the server handles these UDP packets,adhering to the connection-less orientation.

Stateful or Stateless

Our communication protocol is designed to be stateful:
it maintains session information and connections states, primarily through the use of unique UUIDs for each session. this is observable in the server management of TCP connections and UDP client sessions.
The stateful nature is further reinforced by methods like registerIncomingMessage and route,which process messages based on the stored session information and previous interactions.

The protocols ability to track and manage ongoing client interactions indicates a departure from a stateless design, where each request is independent.

Conclusion
In summary,our communication protocol integrates both connection-oriented (TCP) and connection-less (UDP) communication strategies. This hybrid approach allows it to leverage the strengths of both TCP (reliability and order) And UDP(efficiency and speed). Additionally,the protocol is stateful,maintaining essential information about client session and interactions,which enables more complex and continuos communication processes.


## Types, constants

TODO - Do you have some specific value types you use in several messages? They you can describe
them here.

Specific Value Types in Message Communication
In our communication protocol,we utilize specific value types across several messages to ensure consistency and robustness in data transmission. These value types are essential for maintaining the integrity and clarity of communication between the server and client.

UUID (Universally unique identifier):
Source and Destination identification: Each message contains a source and destination field,both of which are UUIDs. The source UUID is sent server-side to ensure global recognition,while the destination UUID specifies the intended  recipient of the message.

Session Management: UUIDs play a crucial role in session management. They are used to uniquely identify client session,facilitating precise and secure communication across the network.

Serializable Payload:
Flexible Data Handling: The payload of a message,represented by the payload field,is of a generic type T that extends Serializable. This design allows for a wide range of data types to be transmitted as the message content, provided they implement the Serializable interface.

Data Integrity: The use of Serializable objects ensures that the data can be reliably sent over the network and reconstructed at the receiving end without loss of information or structure.

Message Flags (isUDP)
Protocol Selection: Additional flags like isUdp in the Message class indicate whether the message should be sent over a UDP connection. This allows the protocol to dynamically choose between TCP and UDP based on the nature of the message,optimizing communication efficiency.

Specialized Message Types (UuidMessage)
Targeted Functionality. The UuidMessage class, a specialized form of Message<UUID>,is used for specific scenarios like transmitting session IDs. This specialization streamlines certain communication processes,ensuring they are handled with the appropriate data type and methods.
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
