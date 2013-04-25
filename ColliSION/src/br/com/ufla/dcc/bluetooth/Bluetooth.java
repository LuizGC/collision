package br.com.ufla.dcc.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class Bluetooth {
	BluetoothAdapter mBluetoothAdapter;
	BluetoothDevice mmDevice;
	BluetoothSocket mmSocket;
	OutputStream mmOutputStream;
	InputStream mmInputStream;
	Boolean stop;
	Thread workerThread;
	byte[] readBuffer;
	int readBufferPosition;
	int counter;
	String data;

	public Bluetooth(String deviceName) {
		this.findBT(deviceName);
	}
	

	public String getData() {
		String aux = new String(data);
		if(data.length() > 10000)
			data = new String();
		return aux;
	}

	private Boolean findBT(String deviceName) {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null)
			return false;

		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
				.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				if (device.getName().equals(deviceName)) {
					mmDevice = device;
					return true;
				}
			}
		}

		return false;
	}

	public Boolean openBT() {
		try {
			UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // Standard
			mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
			mmSocket.connect();
			mmOutputStream = mmSocket.getOutputStream();
			mmInputStream = mmSocket.getInputStream();
			beginListenForData();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			stop = true;
			return false;
		}

	}

	private void beginListenForData() {
		data = "";
		stop = false;
		readBufferPosition = 0;
		readBuffer = new byte[1024];
		workerThread = new Thread(new Runnable() {
			public void run() {
				while (!stop) {
					try {
						int bytesAvailable = mmInputStream.available();
						if (bytesAvailable > 0) {
							byte[] packetBytes = new byte[bytesAvailable];
							mmInputStream.read(packetBytes);
							for (int i = 0; i < bytesAvailable; i++) {
								byte b = packetBytes[i];
								data += (char) b;
							}
						}
					} catch (Exception ex) {
						stop = true;
						ex.printStackTrace();
					}
				}
			}
		});

		workerThread.start();
	}

	public void sendData(String data) {
		try {
			String msg = String.valueOf(data);
			mmOutputStream.write(msg.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Boolean closeBT() {
		try {
			stop = true;
			mmOutputStream.close();
			mmInputStream.close();
			mmSocket.close();
			workerThread.interrupt();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}
