package com.ms.practice;


import java.util.concurrent.*;
import java.util.*;

enum Priority
{
    High, Medium, Low;
}

public class PriorityBlockingQueue {
	
	public static void main(String args[]) throws InterruptedException {
		
		PriorityBlockingQueue PBQueue = new PriorityBlockingQueue();
		
		Thread p1 = new Thread( new Runnable() {
			public void run() {
				Producer prod = PBQueue.new Producer("Producer1");
				prod.produceMessage(Priority.High);
			}
		});
		Thread p2 = new Thread(new Runnable() {
			public void run() {
				Producer prod = PBQueue.new Producer("Producer2");
				prod.produceMessage(Priority.Medium);
			}
		});
		
		Thread p3 = new Thread(new Runnable() {
			public void run() {
				Producer prod = PBQueue.new Producer("Producer3");
				prod.produceMessage(Priority.Low);
			}
		});
		
		Consumer con1 = PBQueue.new Consumer("Consumer1");
		Delegator.Register(con1, Priority.Low);
		
		Consumer con2 = PBQueue.new Consumer("Consumer2");
		Delegator.Register(con2, Priority.Medium);
		
		
		Consumer con3 = PBQueue.new Consumer("Consumer3");
		Delegator.Register(con3, Priority.High);
		
		p1.start();
		p2.start();
		p3.start();
		
		Delegator.startMsgDelivery();
		
	}
	
	public class Consumer{
		String name;
		public Consumer(String name) {			
			this.name = name;
		}
		
		public void push(Integer data, Priority prio) {
			System.out.println("Consumer "+ name + " with " + data + " of " + prio.toString());	
		}
	}
	
	public	class Producer{
		int value1 = 0;
		int value2 = 0;
		int value3 = 0;
		
		String name;
		
		public Producer(String name) {			
			this.name = name;
		}
		
		public void produceMessage(Priority prio) {			
			while(true) {
				switch(prio){
					case High:{
						++value1;
						Delegator.put(prio, value1);
						System.out.println("Producer "+ name + " with value " + value1 + " of Priority " + prio.toString());
						break;
					}
					case Medium:{
						++value2;
						Delegator.put(prio, value2);
						System.out.println("Producer "+ name + " with value " + value2 + " of Priority " + prio.toString());
						break;
					}
					case Low:{
						++value3;
						Delegator.put(prio, value3);
						System.out.println("Producer "+ name + " with value " + value3 + " of Priority " + prio.toString());
						break;
					}
				}	
				/*try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			}
		}
	}
	
	public static class Delegator{
		
		private static BlockingQueue<Integer> queueHigh = new ArrayBlockingQueue<Integer>(1024);
		private static BlockingQueue<Integer> queueMedium = new ArrayBlockingQueue<Integer>(1024);
		private static BlockingQueue<Integer> queueLow = new ArrayBlockingQueue<Integer>(1024);
		
		private static HashMap<Object, Priority> consuHigh = new HashMap<Object, Priority>();
		private static HashMap<Object,Priority> consuMedium = new HashMap<Object,Priority>();
		private static HashMap<Object, Priority> consuLow = new HashMap<Object, Priority>();
		/*LinkedList<Object> consuMedium = new LinkedList<Object>();
		LinkedList<Object> consuLow = new LinkedList<Object>();*/
		
		
		public static void put(Priority prio, int value) {
			try {
				switch(prio){
					case High:{
						queueHigh.put(value);	
						break;
					}
					case Medium:{
						queueMedium.put(value);
						break;
					}
					case Low:{
						queueLow.put(value);
						break;
					}
					default:{}
				}
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}
		
		public static void Register(Object consumer, Priority prio) {
			switch(prio){
				case High:{
					if (!consuHigh.containsKey(consumer)) {
						consuHigh.put(consumer, prio);
					}
					break;
				}
				case Medium:{
					if (!consuMedium.containsKey(consumer)) {
						consuMedium.put(consumer, prio);
					}	
					break;
				}
				case Low:{
					if (!consuLow.containsKey(consumer)) {
						consuLow.put(consumer, prio);
					}
					break;
				}
				default:{}
			}			
		}
		
		public static void startMsgDelivery() {
			readHighPrio();
			readMediumPrio();
			readLowPrio();
		}
		
		private static void readHighPrio() {			
			Thread t = new Thread(new Runnable() {
				public void run() {
					int data ;
					while(true) {
						try {
							data = queueHigh.take();
							Iterator<Object> iter = consuHigh.keySet().iterator();
							while(iter.hasNext()) {
								// Push the data to the next consumer in the set
								((Consumer)iter.next()).push(data, Priority.High);
							}
						}
						catch(InterruptedException e) {
							e.printStackTrace();
						}	
					}
				}
			});
			
			t.start();
		}
		
		private static void readMediumPrio() {
			Thread t = new Thread( new Runnable() {
				
				@Override
				public void run() {
					int data ;
					while(true) {
						try {
							data = queueMedium.take();
							Iterator<Object> iter = consuMedium.keySet().iterator();
							while(iter.hasNext()) {
								// Push the data to the next consumer in the set
								((Consumer)iter.next()).push(data, Priority.Medium);
							}
						}
						catch(InterruptedException e) {
							e.printStackTrace();
						}
					}
					
				}
			});
			
			t.start();
			
		}
		
		private static void readLowPrio() {
			
			Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					int data ;
					while(true) {
						try {
							data = queueLow.take();
							Iterator<Object> iter = consuLow.keySet().iterator();
							while(iter.hasNext()) {
								// Push the data to the next consumer in the set
								((Consumer)iter.next()).push(data, Priority.Low);
							}
						}
						catch(InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			});
			
			t.start();
									
		}
	}
	
}
