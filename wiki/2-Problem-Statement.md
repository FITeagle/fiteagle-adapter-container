## Adapters
Functionality is added to the FITeagle system using adapters which are located in the southbound hemisphere (see Project Background). As seen in the image below, adapters are connected to the FITeagle system over its message bus by subscribing to relevant topics. Adapters represent an interface between the corresponding resource and the FITeagle system.

![Fig. 2](firma-img-2.png)

There are already a number of adapters providing their functionality to FITeagle. For example, OpenStack and TOSCA adapters. The goal of this project is to provide the light virtualization functions of Docker to the FITeagle ecosystem. In order to do so, we must implement a Docker client which allows us to communicate with Docker and implement a FITeagle Adapter to provide an usable interface.
