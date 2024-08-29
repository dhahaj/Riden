/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.dhahaj;

import java.io.IOException;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersResponse;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.util.SerialParameters;



/**
 *
 * @author dhahaj
 */
public class Java {

    public static void main(String[] args) {

        SerialParameters params = new SerialParameters();

        params.setPortName("COM16");
        params.setBaudRate(115200);
        params.setDatabits(8);
        params.setParity("None");
        params.setStopbits(1);

        params.setEncoding(Modbus.SERIAL_ENCODING_RTU);
        params.setEcho(true);
        System.out.println("Starting...");
        SerialConnection connection = new SerialConnection(params);
        try {
            connection.open();

            int unitId =1;
            int startAddress = 1;
            int count = 1;
            ReadInputRegistersRequest request = new ReadInputRegistersRequest(startAddress, count);

            request.setUnitID(unitId);
            request.setHeadless();

            ModbusSerialTransaction transaction = new ModbusSerialTransaction(connection);
            transaction.setRequest(request);

            transaction.execute();
            ReadInputRegistersResponse response = (ReadInputRegistersResponse) transaction.getResponse();

            for (int n=0;n<response.getWordCount();n++) {
                System.out.println("Register " + n + ": " + response.getRegisters()[n]);
            }

        } catch (ModbusException | IOException ex) {
            System.err.println(ex.getMessage());
        } finally {
            connection.close();
        }

    }
}
