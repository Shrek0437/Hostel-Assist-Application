# Hostel-Assist-Application

Hostel Assist is a **modular**, **distributed systems–based** application designed to demonstrate multiple **inter-process** and **inter-system communication** models through a realistic hostel utility use case.
The project integrates **five independent modules**, each showcasing a different distributed communication paradigm, under a single unified dashboard.

## High-Level System Architecture

- Each module is implemented as an **independent subsystem**.
- A central **Java Swing** dashboard acts as the unified entry point.
- Backend services are **long-running**.
- Client interfaces are launched on demand.
- **No centralized database** is used; all data is in-memory.

## Modules Overview

Each module solves a specific hostel-related problem using a different distributed communication model.

---

### Module - 1: Hostel Complaint Management System

**Communication Model**: Socket Programming (TCP)

**Problem** : Students need a way to submit hostel complaints (water, electricity, cleanliness, etc.) digitally.
**Solution** : A client-server application where the student lodges complaints via a desktop UI application, and a server receives and stores the complaints.

**Features**

1. A simple desktop UI is developed using `Java Swing` which acts as an interface for the client to file complaints.
2. A TCP `socket-based communication` is established between the client and the server.
3. The server is also capable of **handling multiple clients** at the same time through `multi-threading`.
4. All the data is stored in an `in-memory data structure` which enables for **faster retrieval** of data.

**System Design**

- A socket communication is first **established** between the `client` and the `server`, the client UI has a **simple form** which asks for the following details; `Room No`, `Category` of complaint and `Complaint Description`.
- On submission of the form the server **receives** the complaint and **stores** the complaint in a `HashMap` data structure.
- The server creates a **unique** `Complaint ID` for each received complaint which acts as the `key` and the `value` is a `Complaint` object, which the server creates an instance for each complaint.
- Through `multi-threading`, the server is also capable of handling **multiple requests** from different clients at the same time therefore making the system **scalable and fast**.
- The system also handles cases where the server is not running or has stopped and notifies the same to the client ensuring clean **error-handling** and **status communication**.

**Architecture Diagram**

<img src="./Architecture-Diagrams/M1%20Architecture%20Diagram.jpeg" width="500px" alt="Module-1_Architecture_Diagram">

---

### Module – 2: Hostel Room Information Service

**Communication Model**: Java RMI (Remote Method Invocation)

**Problem** :
Students require a simple and efficient way to retrieve hostel room information such as room occupancy and warden contact details without physically visiting the hostel office or relying on notice boards from the Stone Age.

**Solution** :
A distributed Java application using Java RMI, where a client-side desktop UI allows students to search for hostel room details by entering a room number. The backend RMI server processes the request and returns the corresponding room and warden information from in-memory storage.

**Features**

1. A simple desktop-based UI developed using Java Swing that allows students to enter a room number.
2. Java RMI-based communication between the client and server for remote data access.
3. The RMI server exposes multiple remote methods to fetch hostel room and warden details.
4. Hostel data is stored using in-memory collections such as HashMap for fast access.
5. Clear separation between client logic, remote interface, and server-side implementation.
6. Ensures modularity and scalability using distributed object-oriented principles.

**System Design**

1. The system follows a client–server architecture using Java RMI.
2. The client application provides a UI with a room number input field and a display area for results.
3. The client looks up the remote object from the RMI Registry and invokes remote methods.
4. The server implements a remote interface that defines methods such as:
5. Fetching room details by room number
6. Retrieving warden contact information
7. Hostel room information is stored in a HashMap, where the key is the room number and the value is a Room object containing occupant names and warden details.
8. The RMI runtime handles stub–skeleton communication, object serialization, and network-level details transparently.

The system demonstrates core concepts of distributed object management and remote method invocation.

## <img src="./Architecture-Diagrams/M2%20Architecture%20Diagram.jpeg" width="500px" alt="Module-1_Architecture_Diagram">

### Module - 3: Hostel Notice Board System

**Communication Model**: Remote-Procedure Call (REST API over HTTP)

**Problem** : Hostel notices need to be published by admins or wardens and viewed by students.
**Solution** : A REST-based RPC system where admins and students invoke remote operations via HTTP.

**Tech Stack**

- **_Frontend_**: ReactJS
- **_Backend_**: Python Flask
- **_Protocol_**: HTTP (GET, POST, PUT, DELETE)

**Features**

1. A simple **web-based** interface where admins can **add,update and delete** notices.
2. Students can also interact with the interface where they can **view** the notices.
3. The whole system uses `REST API`, which follows a `state-less architecture`, meaning the server **does not store any information** about the client's session or other interactions.
4. All the data is stored in an `in-memory data structure` which enables for **faster retrieval** of data.

**System Design**

- The client communicates with a **remote server** via a web interface, where the client has **two roles**, either the client can be an **Admin** or a **Student**. An admin has the functionalities of creating, updating and deleting notices whereas a student can only view the notices.
- The **remote servers listens for requests** from both these clients.
- The client sends requests through `HTTP` methods
  i. `createNotice` -> `POST` method
  ii. `updateNotice` -> `PUT` method
  iii. `deleteNotice` -> `DELETE` method
  iv. `viewNotice` -> `GET` method.
- Based on these methods the server **executes** the necessary functions and **sends acknowledgements** back to the clients.
- On receival of notices the server creates an instance of the notice and assigns a unique `noticeID` for each notice created and stores in a `dictionary` where the key is the `noticeID` and the value is the notice object.
- All data is stored as an in-memory data structure and the data is lost when the server stops.

**Architecture Diagram**

<img src="./Architecture-Diagrams/M3%20Architecture%20Diagram.png" width="500px" alt="Module-3_Architecture_Diagram">

---

### Module - 4: Student Resource Sharing System

**Communication Model**: Peer-to-Peer (P2P) Communication over TCP
Problem

**Problem** : Students need an efficient way to share academic resources such as PDFs, notes, and study materials directly with each other. Using a centralized server for file storage can lead to high load, bottlenecks, and a single point of failure.
**Solution** : A Peer-to-Peer (P2P) based resource sharing system is developed where students directly exchange files with one another. Each peer acts as both a client and a server, enabling decentralized file sharing without relying on a central storage server.

**Tech Stack**

- **_Language_**: Java
- **_UI_**: HTML,CSS
- **_Networking_**: Java Sockets
- **_Protocol_**: TCP
- **_Architecture_**: Peer-to-Peer (Decentralized)

**Features**

1. A simple **web-based** interface where students can **upload,view and download** resources.
2. Students can **download** files from other peers.
3. Each peer functions as both **client and server**.
4. No **centralized** file storage server.
5. Multiple peers can run and communicate simultaneously.

**System Design**

- The system follows a **Peer-to-Peer** architecture where every node has equal responsibility.
- The system consists of two roles:
  i. Server Role: Listens for incoming file requests from other peers and sends requested files directly to the requesting peer.
  ii. Client Role: Connects to another peer using IP address and port and requests and downloads files.
- The system uses TCP sockets for reliable file transfer where each peer maintains a local shared directory where uploaded files are stored and made available to other peers.

**Architecture Diagram**

<img src="./Architecture-Diagrams/M4%20Architecture%20Diagram.jpeg" width="500px" alt="Module-4_Architecture_Diagram">

---

### Module - 5: Mess Feedback Live Counter System

**Communication Model**: Shared Memory using Memory-Mapped File

**Problem** : Students need a way to submit mess feedback (Good, Average, Poor) digitally and the management needs to view live feedback count.
**Solution** : A shared-memory based system where multiple student applications update common feedback counters and a display application shows live results.

**Tech Stack**

- **_Language_**: Java
- **_UI_**: Java Swing
- **_Shared Memory_**: Memory-Mapped File (MappedByteBuffer)
- **_Synchronization_**: File Lock (FileLock)
- **_Architecture_**: Multi-process shared memory system

**Features**

1. A simple **desktop UI** is developed using Java Swing for **submitting** feedback.
2. A separate Java Swing UI displays **live feedback** counts.
3. **Multiple client** processes can run simultaneously.
4. `Shared memory` is implemented using a `memory-mapped file`.
5. **File locking** is used to prevent `race` conditions.
6. All data is stored in a **shared in-memory** file for **fast access**.
7. The display updates automatically in real time.

**System Design**

- The system consists of two applications:
  i. **FeedbackUI** for submitting feedback.
  ii. **DisplayUI** for viewing live feedback counts.
- It also has a **shared memory** file (feedback.dat), which is used to store the following data
  i. `Good count`
  ii. `Average count`
  iii. `Poor count`
- The system is also designed to process **multiple feedbacks** with proper **synchronization** and **without any conflicts**.
- The **FeedbackUI**, accesses the same shared memory file and **updates** the counters using synchronization.
- The **DisplayUI**, reads the shared memory **every second** and displays the **live** updated counters.
- The shared memory logic is implemented in `SharedFeedback.java` using `MappedByteBuffer`, `FileChannela`, `FileLock` and `Synchronization`.
- The system **effectively** prevents `race conditions` and maintains data **consistency** throughout the course of communication.

**Architecture Diagram**

<img src="./Architecture-Diagrams/M5%20Architecture%20Diagram.jpeg" width="500px" alt="Module-5_Architecture_Diagram">
