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
