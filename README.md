# Cash Desk Module

[![Java Version](https://img.shields.io/badge/Java-8-blue)](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
[![Maven](https://img.shields.io/badge/Maven-2.6.7-brightgreen)](https://maven.apache.org/)
[![Lombok](https://img.shields.io/badge/Lombok-yellowgreen)](https://projectlombok.org/)
[![ModelMapper](https://img.shields.io/badge/ModelMapper-3.2.0-orange)](http://modelmapper.org/)

## Overview

**Cash Desk Module** is a Java-based project designed to manage cash desk operations efficiently. Utilizing Java 8 and built with Maven, this module leverages Lombok for boilerplate code reduction and ModelMapper for seamless object mapping.

## Features

- **Cash Transaction Management**: Handle cash transactions seamlessly.
- **Integration Ready**: Easily integrates with other systems.
- **Efficient Object Mapping**: Using ModelMapper for converting DTOs to entities and vice versa.
- **Reduced Boilerplate**: Using Lombok to minimize boilerplate code.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Installation](#installation)

[//]: # (- [Usage]&#40;#usage&#41;)

[//]: # (- [Contributing]&#40;#contributing&#41;)

[//]: # (- [License]&#40;#license&#41;)

## Prerequisites

Ensure you have the following installed:

- Java 8
- Maven 2.6.7

## Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/santush87/CashDeskModule.git
   cd CashDeskModule

2. Build the project using Maven:

    ```bash
   mvn clean install

[//]: # (## Usage)

[//]: # ()
[//]: # (To run the module, execute the following command:)

[//]: # ()
[//]: # (   ```bash)

[//]: # (   mvn exec:java -Dexec.mainClass="com.martin.aleksandrov.CashDeskModule.CashDeskModuleApplication" )

[//]: # (   ```)
## Files Generated
Upon starting the program, the following file will be generated:

1. **BALANCE.txt**: This file will contain the initial balance data and a user named MARTINA.

   After the first request, an additional file will be generated:
2. **TRANSACTION.txt**: This file will store the transaction records.

   After each request, both files will be updated accordingly.

## Postman Collection
The project includes a Postman collection for testing the module's functionalities. The collection file is named Cash desk module.postman_collection.json and contains the following requests:

1. **Withdrawal 1**: Withdraw 100 BGN, denomination: 5x10 BGN, 1x50 BGN.
2. **Withdrawal 2**: Withdraw 500 EUR, denomination: 10x50 EUR.
3. **Deposit 1**: Deposit 600 BGN, denomination: 10x10 BGN, 10x50 BGN.
4. **Deposit 2**: Deposit 200 BGN, denomination: 5x20 EUR, 2x50 EUR.
5. **Check Balance**: Check the balance and denominations.

## Using the Postman Collection
1. Open Postman.
2. Import the collection by clicking on Import in the top left corner.
3. Select the **Cash desk module.postman_collection.json** file from your file system.
4. The collection will appear in your Postman workspace. You can now run the requests to interact with the CashDeskModule.

[//]: # (## Configuration)

[//]: # (Configuration details and properties can be found in the src/main/resources directory. Adjust the settings according to your environment requirements.)

## Lombok
Lombok annotations are used to reduce boilerplate code. Ensure your IDE is configured to handle Lombok annotations. Refer to [Lombok Setup](https://projectlombok.org/setup/overview) for more details.

[//]: # (## Contributing)

[//]: # (Contributions are welcome! Please open an issue or submit a pull request for any changes or improvements.)
