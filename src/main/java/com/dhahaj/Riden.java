package com.dhahaj;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.msg.WriteMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.WriteMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterRequest;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterResponse;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import com.ghgande.j2mod.modbus.util.SerialParameters;

public class Riden {
    private int address;
    private SerialConnection connection;
    private boolean closeAfterCall;

    public Riden(String portName, int baudRate, int address, boolean closeAfterCall) {
        this.address = address;
        this.closeAfterCall = closeAfterCall;

        SerialParameters params = new SerialParameters();
        params.setPortName(portName);
        params.setBaudRate(baudRate);
        params.setDatabits(8);
        params.setParity("None");
        params.setStopbits(1);
        params.setEncoding(Modbus.SERIAL_ENCODING_RTU);
        params.setEcho(false);
        connection = new SerialConnection(params);
        try {
            connection.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   public Map<Integer, Integer> readAllRegisters(int start, int count) {
        Map<Integer, Integer> registerValues = new LinkedHashMap<>(); // Changed to LinkedHashMap
        try {
            ReadMultipleRegistersRequest request = new ReadMultipleRegistersRequest(start, count);
            request.setUnitID(this.address);
            request.setHeadless();

            ModbusSerialTransaction transaction = new ModbusSerialTransaction(connection);
            transaction.setRequest(request);

            if (closeAfterCall) {
                connection.open();
            }

            transaction.execute();

            if (closeAfterCall) {
                connection.close();
            }

            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse) transaction.getResponse();
            Register[] registers = response.getRegisters();
            for (int i = 0; i < registers.length; i++) {
                registerValues.put(start + i, registers[i].getValue());
            }

        } catch (Exception e) {
            System.out.println("Error reading registers: " + e.getMessage());
        }
        return registerValues;
    }

    public void writeMultipleRegisters(int start, int[] values) {
        try {
            // Create an array of registers
            Register[] registers = new SimpleRegister[values.length];
            for (int i = 0; i < values.length; i++) {
                registers[i] = new SimpleRegister(values[i]);
            }

            WriteMultipleRegistersRequest request = new WriteMultipleRegistersRequest(start, registers);
            request.setUnitID(this.address);
            request.setHeadless();

            ModbusSerialTransaction transaction = new ModbusSerialTransaction(connection);
            transaction.setRequest(request);

            if (closeAfterCall) {
                connection.open();
            }

            transaction.execute();

            if (closeAfterCall) {
                connection.close();
            }

            WriteMultipleRegistersResponse response = (WriteMultipleRegistersResponse) transaction.getResponse();
            System.out.println("Write operation was successful, status=" + response.getHexMessage());
        } catch (Exception e) {
            System.out.println("Error writing multiple registers: " + e.getMessage());
        }
    }

    public void write(int reg, int value) {
        try {
            Register registers = new SimpleRegister(value);
        WriteSingleRegisterRequest request = new WriteSingleRegisterRequest(reg, registers);
        request.setUnitID(this.address);
        request.setHeadless();

        ModbusSerialTransaction transaction = new ModbusSerialTransaction(connection);
        transaction.setRequest(request);

        if (closeAfterCall) {
            connection.open();
        }

            transaction.execute();
            
            if (closeAfterCall) {
                connection.close();
            }
            
            WriteSingleRegisterResponse response = (WriteSingleRegisterResponse) transaction.getResponse();
            System.out.println("Write operation was successful, status=" + response.getHexMessage());
        } catch (Exception e) {
            System.out.println("Error writing single register: " + e.getMessage());
        }
        
    }

    public static void main(String[] args) {
        try {
            Riden device = new Riden("COM16", 115200, 1, false);
            Map<Integer, Integer> allRegisterValues = device.readAllRegisters(30, 14); // Assuming you want to read the first 256 registers
            allRegisterValues.forEach((reg, value) -> System.out.println("Register " + reg + ": " + value));

            // int[] valuesToWrite = {1}; //700, 1000, 0, 0, 0, 0, 0, 0, 0, 0};
            device.write(8, 650);
            device.write(9, 500);

            device.write(18, 1);

            Thread.sleep(3000);

            device.write(8, 900);
            device.write(9, 100);
            Thread.sleep(3000);
            device.write(18, 0);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


