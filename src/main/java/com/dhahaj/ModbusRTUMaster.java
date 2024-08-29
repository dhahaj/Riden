package com.dhahaj;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.util.SerialParameters;

public class ModbusRTUMaster {
    public static void main(String[] args) {
        SerialParameters params = new SerialParameters();
        params.setPortName("COM16");
        params.setBaudRate(115200);
        params.setDatabits(8);
        params.setParity("None");
        params.setStopbits(1);
        params.setEncoding(Modbus.SERIAL_ENCODING_RTU);
        params.setEcho(false);

        SerialConnection connection = new SerialConnection(params);

        try {
            connection.open();
            int unitId = 1;
            int startAddress = 0;
            int count = 10;  // Example: Read 10 registers

            ReadMultipleRegistersRequest request = new ReadMultipleRegistersRequest(startAddress, count);
            request.setUnitID(unitId);
            request.setHeadless();

            ModbusSerialTransaction transaction = new ModbusSerialTransaction(connection);
            transaction.setRequest(request);

            transaction.execute();
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse) transaction.getResponse();

            if (response != null) {
                for (int n = 0; n < response.getWordCount(); n++) {
                    System.out.println("Value[" + n + "] = " + response.getRegisterValue(n));
                }
            } else {
                System.out.println("Response was null, check unit ID and address.");
            }

        } catch (Exception ex) {
            System.out.println("Error during Modbus transaction: " + ex.getMessage());
        } finally {
            if (connection.isOpen()) {
                connection.close();
            }
        }
    }
}


