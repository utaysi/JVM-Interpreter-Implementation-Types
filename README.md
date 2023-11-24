# JVM Interpreter Implementation Types

## Overview
This repository presents a suite of Java Virtual Machine (JVM) interpreters developed as a collaborative project for the 'Virtual Machines' course at Philipps University of Marburg. It showcases the implementation and performance comparison of three distinct interpreter types:

- **Decode-Dispatch Interpreter**: A traditional approach where each instruction is decoded and executed in a step-by-step manner.
- **Indirect-Threaded Interpreter**: Leverages a method dispatch table to streamline instruction execution.
- **Direct-Threaded Interpreter**: Employs computed gotos for instruction handling, thereby reducing dispatch overhead.

We tested these interpreters using four different programs, enabling a detailed analysis of their respective efficiencies and execution characteristics.

## Performance Insights
The comparative analysis underscored fascinating performance trade-offs among the interpreter types, illuminating the impact of design choices on execution dynamics.

![conclusion-table](/images/conclusion-table.png)

## Project Contributors
- **Mohamad Kaser**
- **Parmida Talebi**
- **Ugur Taysi**

## Source Material
For more information on the JVM instruction set, refer to [Chapter 6: The Java Virtual Machine Instruction Set](https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-6.html).
