/* This File is part of the wayoda-project                                    */
/* Filename : ArrayQueue.java                                                 */
/* Copyright (C) <2002>  <Eberhard Fahle>
   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA  */

package org.wayoda.value;

/**
 * Abstract baseclass for queues that work on the primitive datatypes in Java.
 * <p>
 * The classes that extend ArrayQueue are simple Queues. The only thing thats special
 * about them is that they work on the primitive datatypes of the java-language.
 * There is an implementation for each of the datatypes : 
 * <code>boolean,byte,short,char,int,long,float and double</code>
 * The ArrayQueue class itself defines common methods for reading the size and capacity
 * of the queue. There are also methods for detecting wether the queue is empty, filled
 * to its capacity, or has room to add new items without deleteing older ones.
 * And it provides also the method to clear all items in the list.
 * The nested classes that extend ArrayQueue provide the methods to append
 * or prepend items, to remove (or read )the item at the top of the queue and to resize
 * the queue. There is also a method to retrieve an array of the datatype for 
 * which the class is implemented, that contains only the items that were appended or prepended
 * to the queue.<p>
 * All the methods in the ArrayQueues for the primitive datatypes are synchronized regarding
 * the data that is stored in the queue.
 * <p>
 * @author Eberhard Fahle
 * @version 0.1
 */
public abstract class ArrayQueue {
		/** the capacity of the queue */
		protected int capacity;
		/** the length of the queue */
		protected int size;
		/** the position of the head of the queue */
		protected int head;
		/** the position of the last value in the queue */
		protected int tail;
				
		/**
		 * Creates a new ArrayQueue.
		 * @param capacity the capacity of the queue to be created. The capacity must be at least 1 because
		 * we want to have at least 1 data-item to write our values to.
		 * @throws IllegalArgumentException if capacity<1
		 */
		public ArrayQueue(int capacity) {
				if(capacity<1)
						throw new IllegalArgumentException("ArrayQueue must have a capacity > 0"); //$NON-NLS-1$
				size=0;
				head=0;	//no item yet
				tail=0;	//here the first item would go
				this.capacity=capacity;
		}

		/**
		 * Gets the current size of the queue. The size returned here is either equal to the number 
		 * of items added to the queue when the queue is not filled completly, or equal to the capacity
		 * of the queue once the queue is full.
		 * @return int the current size of the queue
		 */
		public synchronized int size() {
				return size;
		}

		/**
		 * Gets the capacity of the queue. This is the 
		 * maximum number of items that can be stored in the queue.
		 */
		public synchronized int capacity() {
				return capacity;
		}

		/**
		 * Tests wether there are unused items in the queue.
		 * @return boolean True if remaining()>0, false otherwise.
		 */
		public synchronized boolean hasRemaining() {
				return (capacity-size)>0;
		}

		/**
		 * Gets the number of items in the queue for which no value has been set yet.
		 * Once the queue is full this will return 0 always.
		 * @return int the number of items in the queue not used yet.
		 */
		public synchronized int remaining() {
				return capacity-size;
		}
		
		/**
		 * Clear all items in the queue.
		 */
		public synchronized void clear() {
				head=0;
				tail=0;
				size=0;
		}

		/**
		 * Resize the queue. If the new capacity is larger or equal to the current 
		 * capacity of the queue all items in the queue will be copied to the beginning 
		 * of the new queue. If the new capacity is smaller than the current capacity 
		 * the method will copy items from the end of the queue. 
		 * Items at the end of the queue are assumed to be <em>newer</em> and therfore
		 * <em>more important</em> than the items from the beginning of the queue.
		 * @param capacity new capacity of the queue  
		 * @throws IllegalArgumentException if capacity<1
		 */
		public abstract void resize(int capacity);

		/**
		 * Tests wether there are no items in the queue yet.
		 * @return boolean True if no items have been added to the queue yet.
		 */
		public synchronized boolean isEmpty() {
				return size==0;
		}
		
		/**
		 * Tests wether the queue is full.
		 * @return boolean True if there are no unused items in the queue.
		 */
		public synchronized boolean isFull() {
				return size==capacity;
		}

		/**************************************************************************/
		/* Nested classes from here on                                            */
		/**************************************************************************/

		/** 
		 * An ArrayQueue for bytes. 
		 */
		public static class Byte extends ArrayQueue implements Cloneable {
				/** our data */
				private byte[] data;

				/**
				 * Creates a new ArrayQueue.Byte. 
				 * @param capacity the capacity of the queue.
				 * The capacity must be at least 1 because we want to have at least 1 data-item to write our values to.
				 * @throws IllegalArgumentException if capacity<1
				 */
				public Byte(int capacity) {
						super(capacity);
						data=new byte[capacity];
				}

				/**
				 * Resize the queue. If the new capacity is larger or equal to the current 
				 * capacity of the queue all items in the queue will be copied to the beginning 
				 * of the new queue. If the new capacity is smaller than the current capacity 
				 * the method will copy items from the end of the queue. 
				 * Items at the end of the queue are assumed to be <em>newer</em> and therfore
				 * <em>more important</em> than the items from the beginning of the queue.
				 * @param capacity new capacity of the queue.  
				 * The capacity must be at least 1 because we want to have at least 1 data-item to write our values to.
				 * @throws IllegalArgumentException if capacity<1
				 */
				public synchronized void resize(int capacity) {
						if(capacity<1)
								throw new IllegalArgumentException("ArrayQueue must have a capacity > 0"); //$NON-NLS-1$
						if(size!=0) {
								byte [] tmp=toArray();
								data=new byte[capacity];
								if(tmp.length>data.length) {
										//copy the items from the end of old queue
										for(int i=0,j=tmp.length-data.length;i<data.length;i++,j++)
												data[i]=tmp[j];
										head=0;
										tail=head;
										size=data.length;
								}
								else {
										for(int i=0;i<tmp.length;i++)
												data[i]=tmp[i];
										head=0;
										tail=tmp.length;
										size=tmp.length;
								}
						}
						else {
								data=new byte[capacity];
								head=0;
								tail=0;
						}
						this.capacity=capacity;
				}

				/**
				 * Append a new byte to the queue. If the queue is full the item on the head of
				 * the queue is lost.
				 * @param val the byte to append to the queue
				 */
				public synchronized void append(byte val) {
						data[tail]=val;
						tail++;
						if(tail==capacity)
								tail=0;
						if(isFull()) {			
								//we were already loaded, so move the head also
								head++;
								if(head==capacity)
										head=0;
						}
						else {
								//only if we were not full already
								size++;
						}
				}
				
				/**
				 * Prepend a new byte to the queue. If the queue is full the item on the tail of
				 * the queue is lost.
				 * @param val the byte to prepend to the beginning of the queue
				 */
				public synchronized void prepend(byte val) {
						if(isEmpty()) {
								//first item, just append it
								append(val);
								return;
						}
						head--;
						if(head<0)
								head=capacity-1;
						data[head]=val;
						if(isFull()) {
								//we were already loaded so move the tail of the queue also
								tail--;
								if(tail<0)
										tail=capacity-1;
						}
						else {
								size++;
						}
				}

				/**
				 * Pops the first item from the queue. The item is removed from the queue and returned.
				 * @return byte the byte at the head of the queue
				 * @throws IndexOutOfBoundsException if this method is called
				 * for an empty queue. 
				 */
				public synchronized byte pop() {
						if(size==0)
								throw new IndexOutOfBoundsException("Queue is empty"); //$NON-NLS-1$
						byte retval=data[head];
						head++;
						if(head==capacity)
								head=0;
						size--;
						return retval;
				}
						
				/**
				 * Gets the first item from the queue. The item is NOT removed from the queue.
				 * @return byte the value of the byte at the head of the queue
				 * @throws IndexOutOfBoundsException if this method is called
				 * for an empty queue. 
				 */
				public synchronized byte peek() {
						if(size==0)
								throw new IndexOutOfBoundsException("Queue is empty"); //$NON-NLS-1$
						return data[head];
				}
						
				/**
				 * Gets an array with all the items in the queue.
				 * The array contains only the items that were apended or prepended
				 * to the queue. 
				 * @return byte[] the array of items in the queue. The length of the 
				 * returned array will be the value that is returned by the size() method.
				 */
				public synchronized byte[] toArray() {
						if(isEmpty())
								return new byte[0];
						byte[] retval=new byte[size];
						int i=head;
						int s=0;
						while(s<size) {
								retval[s]=data[i];
								i++;
								if(i==capacity)
										i=0;
								s++;
						}
						return retval;
				}

				/**
				 * Gets a String representation of the queue
				 * @return String a String representation of the queue
				 */
				public String toString() {
						byte [] tmp=toArray();
						StringBuffer sb=new StringBuffer();
						sb.append("ArrayQueue.Byte Capacity=" //$NON-NLS-1$
											+String.valueOf(capacity())
											+" Size=" //$NON-NLS-1$
											+String.valueOf(size())
											+" Remaining=" //$NON-NLS-1$
											+String.valueOf(remaining())
											+" ["); //$NON-NLS-1$
						for(int i=0;i<tmp.length;i++)
								sb.append(String.valueOf(tmp[i])+","); //$NON-NLS-1$
						sb.append("]"); //$NON-NLS-1$
						return sb.toString();
				}
				
				/**
				 * The implementation of the Cloneable interface.
				 * @return Object a new ArrayQueue.Byte that has the same
				 * capacity and items as the one for which it is called.
				 */
				public Object clone() {
						ArrayQueue.Byte cl=new ArrayQueue.Byte(capacity);
						//now copy the items, if any
						byte [] tmp=toArray();
						for(int i=0;i<tmp.length;i++)
								cl.append(tmp[i]);
						return cl;
				}
		}

		/** 
		 * An ArrayQueue for shorts. 
		 */
		public static class Short extends ArrayQueue implements Cloneable {
				/** our data */
				private short[] data;

				/**
				 * Creates a new ArrayQueue.Short. 
				 * @param capacity the capacity of the queue.
				 * The capacity must be at least 1 because we want to have at least 1 data-item to write our values to.
				 * @throws IllegalArgumentException if capacity<1
				 */
				public Short(int capacity) {
						super(capacity);
						data=new short[capacity];
				}

				/**
				 * Resize the queue. If the new capacity is larger or equal to the current 
				 * capacity of the queue all items in the queue will be copied to the beginning 
				 * of the new queue. If the new capacity is smaller than the current capacity 
				 * the method will copy items from the end of the queue. 
				 * Items at the end of the queue are assumed to be <em>newer</em> and therfore
				 * <em>more important</em> than the items from the beginning of the queue.
				 * @param capacity new capacity of the queue.  
				 * The capacity must be at least 1 because we want to have at least 1 data-item to write our values to.
				 * @throws IllegalArgumentException if capacity<1
				 */
				public synchronized void resize(int capacity) {
						if(capacity<1)
								throw new IllegalArgumentException("ArrayQueue must have a capacity > 0"); //$NON-NLS-1$
						if(size!=0) {
								short [] tmp=toArray();
								data=new short[capacity];
								if(tmp.length>data.length) {
										//copy the items from the end of old queue
										for(int i=0,j=tmp.length-data.length;i<data.length;i++,j++)
												data[i]=tmp[j];
										head=0;
										tail=head;
										size=data.length;
								}
								else {
										for(int i=0;i<tmp.length;i++)
												data[i]=tmp[i];
										head=0;
										tail=tmp.length;
										size=tmp.length;
								}
						}
						else {
								data=new short[capacity];
								head=0;
								tail=0;
						}
						this.capacity=capacity;
				}

				/**
				 * Append a new short to the queue. If the queue is full the item on the head of
				 * the queue is lost.
				 * @param val the short to append to the queue
				 */
				public synchronized void append(short val) {
						data[tail]=val;
						tail++;
						if(tail==capacity)
								tail=0;
						if(isFull()) {			
								//we were already loaded, so move the head also
								head++;
								if(head==capacity)
										head=0;
						}
						else {
								//only if we were not full already
								size++;
						}
				}
				
				/**
				 * Prepend a new short to the queue. If the queue is full the item on the tail of
				 * the queue is lost.
				 * @param val the short to prepend to the beginning of the queue
				 */
				public synchronized void prepend(short val) {
						if(isEmpty()) {
								//first item, just append it
								append(val);
								return;
						}
						head--;
						if(head<0)
								head=capacity-1;
						data[head]=val;
						if(isFull()) {
								//we were already loaded so move the tail of the queue also
								tail--;
								if(tail<0)
										tail=capacity-1;
						}
						else {
								size++;
						}
				}

				/**
				 * Pops the first item from the queue. The item is removed from the queue and returned.
				 * @return short the short at the head of the queue
				 * @throws IndexOutOfBoundsException if this method is called
				 * for an empty queue. 
				 */
				public synchronized short pop() {
						if(size==0)
								throw new IndexOutOfBoundsException("Queue is empty"); //$NON-NLS-1$
						short retval=data[head];
						head++;
						if(head==capacity)
								head=0;
						size--;
						return retval;
				}
						
				/**
				 * Gets the first item from the queue. The item is NOT removed from the queue.
				 * @return short the value of the short at the head of the queue
				 * @throws IndexOutOfBoundsException if this method is called
				 * for an empty queue. 
				 */
				public synchronized short peek() {
						if(size==0)
								throw new IndexOutOfBoundsException("Queue is empty"); //$NON-NLS-1$
						return data[head];
				}
						
				/**
				 * Gets an array with all the items in the queue.
				 * The array contains only the items that were apended or prepended
				 * to the queue. 
				 * @return short[] the array of items in the queue. The length of the 
				 * returned array will be the value that is returned by the size() method.
				 */
				public synchronized short[] toArray() {
						if(isEmpty())
								return new short[0];
						short[] retval=new short[size];
						int i=head;
						int s=0;
						while(s<size) {
								retval[s]=data[i];
								i++;
								if(i==capacity)
										i=0;
								s++;
						}
						return retval;
				}

				/**
				 * Gets a String representation of the queue
				 * @return String a String representation of the queue
				 */
				public String toString() {
						short [] tmp=toArray();
						StringBuffer sb=new StringBuffer();
						sb.append("ArrayQueue.Short Capacity=" //$NON-NLS-1$
											+String.valueOf(capacity())
											+" Size=" //$NON-NLS-1$
											+String.valueOf(size())
											+" Remaining=" //$NON-NLS-1$
											+String.valueOf(remaining())
											+" ["); //$NON-NLS-1$
						for(int i=0;i<tmp.length;i++)
								sb.append(String.valueOf(tmp[i])+","); //$NON-NLS-1$
						sb.append("]"); //$NON-NLS-1$
						return sb.toString();
				}
				
				/**
				 * The implementation of the Cloneable interface.
				 * @return Object a new ArrayQueue.Short that has the same
				 * capacity and items as the one for which it is called.
				 */
				public Object clone() {
						ArrayQueue.Short cl=new ArrayQueue.Short(capacity);
						//now copy the items, if any
						short [] tmp=toArray();
						for(int i=0;i<tmp.length;i++)
								cl.append(tmp[i]);
						return cl;
				}
		}

		/** 
		 * An ArrayQueue for ints. 
		 */
		public static class Int extends ArrayQueue implements Cloneable {
				/** our data */
				private int[] data;

				/**
				 * Creates a new ArrayQueue.Int. 
				 * @param capacity the capacity of the queue.
				 * The capacity must be at least 1 because we want to have at least 1 data-item to write our values to.
				 * @throws IllegalArgumentException if capacity<1
				 */
				public Int(int capacity) {
						super(capacity);
						data=new int[capacity];
				}

				/**
				 * Resize the queue. If the new capacity is larger or equal to the current 
				 * capacity of the queue all items in the queue will be copied to the beginning 
				 * of the new queue. If the new capacity is smaller than the current capacity 
				 * the method will copy items from the end of the queue. 
				 * Items at the end of the queue are assumed to be <em>newer</em> and therfore
				 * <em>more important</em> than the items from the beginning of the queue.
				 * @param capacity new capacity of the queue.  
				 * The capacity must be at least 1 because we want to have at least 1 data-item to write our values to.
				 * @throws IllegalArgumentException if capacity<1
				 */
				public synchronized void resize(int capacity) {
						if(capacity<1)
								throw new IllegalArgumentException("ArrayQueue must have a capacity > 0"); //$NON-NLS-1$
						if(size!=0) {
								int [] tmp=toArray();
								data=new int[capacity];
								if(tmp.length>data.length) {
										//copy the items from the end of old queue
										for(int i=0,j=tmp.length-data.length;i<data.length;i++,j++)
												data[i]=tmp[j];
										head=0;
										tail=head;
										size=data.length;
								}
								else {
										for(int i=0;i<tmp.length;i++)
												data[i]=tmp[i];
										head=0;
										tail=tmp.length;
										size=tmp.length;
								}
						}
						else {
								data=new int[capacity];
								head=0;
								tail=0;
						}
						this.capacity=capacity;
				}

				/**
				 * Append a new int to the queue. If the queue is full the item on the head of
				 * the queue is lost.
				 * @param b the int to append to the queue
				 */
				public synchronized void append(int val) {
						data[tail]=val;
						tail++;
						if(tail==capacity)
								tail=0;
						if(isFull()) {			
								//we were already loaded, so move the head also
								head++;
								if(head==capacity)
										head=0;
						}
						else {
								//only if we were not full already
								size++;
						}
				}
				
				/**
				 * Prepend a new int to the queue. If the queue is full the item on the tail of
				 * the queue is lost.
				 * @param val the int to prepend to the beginning of the queue
				 */
				public synchronized void prepend(int val) {
						if(isEmpty()) {
								//first item, just append it
								append(val);
								return;
						}
						head--;
						if(head<0)
								head=capacity-1;
						data[head]=val;
						if(isFull()) {
								//we were already loaded so move the tail of the queue also
								tail--;
								if(tail<0)
										tail=capacity-1;
						}
						else {
								size++;
						}
				}

				/**
				 * Pops the first item from the queue. The item is removed from the queue and returned.
				 * @return int the int at the head of the queue
				 * @throws IndexOutOfBoundsException if this method is called
				 * for an empty queue. 
				 */
				public synchronized int pop() {
						if(size==0)
								throw new IndexOutOfBoundsException("Queue is empty"); //$NON-NLS-1$
						int retval=data[head];
						head++;
						if(head==capacity)
								head=0;
						size--;
						return retval;
				}
						
				/**
				 * Gets the first item from the queue. The item is NOT removed from the queue.
				 * @return int the value of the int at the head of the queue
				 * @throws IndexOutOfBoundsException if this method is called
				 * for an empty queue. 
				 */
				public synchronized int peek() {
						if(size==0)
								throw new IndexOutOfBoundsException("Queue is empty"); //$NON-NLS-1$
						return data[head];
				}
						
				/**
				 * Gets an array with all the items in the queue.
				 * The array contains only the items that were apended or prepended
				 * to the queue. 
				 * @return int[] the array of items in the queue. The length of the 
				 * returned array will be the value that is returned by the size() method.
				 */
				public synchronized int[] toArray() {
						if(isEmpty())
								return new int[0];
						int[] retval=new int[size];
						int i=head;
						int s=0;
						while(s<size) {
								retval[s]=data[i];
								i++;
								if(i==capacity)
										i=0;
								s++;
						}
						return retval;
				}

				/**
				 * Gets a String representation of the queue
				 * @return String a String representation of the queue
				 */
				public String toString() {
						int [] tmp=toArray();
						StringBuffer sb=new StringBuffer();
						sb.append("ArrayQueue.Int Capacity=" //$NON-NLS-1$
											+String.valueOf(capacity())
											+" Size=" //$NON-NLS-1$
											+String.valueOf(size())
											+" Remaining=" //$NON-NLS-1$
											+String.valueOf(remaining())
											+" ["); //$NON-NLS-1$
						for(int i=0;i<tmp.length;i++)
								sb.append(String.valueOf(tmp[i])+","); //$NON-NLS-1$
						sb.append("]"); //$NON-NLS-1$
						return sb.toString();
				}
				
				/**
				 * The implementation of the Cloneable interface.
				 * @return Object a new ArrayQueue.Int that has the same
				 * capacity and items as the one for which it is called.
				 */
				public Object clone() {
						ArrayQueue.Int cl=new ArrayQueue.Int(capacity);
						//now copy the items, if any
						int [] tmp=toArray();
						for(int i=0;i<tmp.length;i++)
								cl.append(tmp[i]);
						return cl;
				}
		}

		/** 
		 * An ArrayQueue for longs. 
		 */
		public static class Long extends ArrayQueue implements Cloneable {
				/** our data */
				private long[] data;

				/**
				 * Creates a new ArrayQueue.Long. 
				 * @param capacity the capacity of the queue.
				 * The capacity must be at least 1 because we want to have at least 1 data-item to write our values to.
				 * @throws IllegalArgumentException if capacity<1
				 */
				public Long(int capacity) {
						super(capacity);
						data=new long[capacity];
				}

				/**
				 * Resize the queue. If the new capacity is larger or equal to the current 
				 * capacity of the queue all items in the queue will be copied to the beginning 
				 * of the new queue. If the new capacity is smaller than the current capacity 
				 * the method will copy items from the end of the queue. 
				 * Items at the end of the queue are assumed to be <em>newer</em> and therfore
				 * <em>more important</em> than the items from the beginning of the queue.
				 * @param capacity new capacity of the queue.  
				 * The capacity must be at least 1 because we want to have at least 1 data-item to write our values to.
				 * @throws IllegalArgumentException if capacity<1
				 */
				public synchronized void resize(int capacity) {
						if(capacity<1)
								throw new IllegalArgumentException("ArrayQueue must have a capacity > 0"); //$NON-NLS-1$
						if(size!=0) {
								long [] tmp=toArray();
								data=new long[capacity];
								if(tmp.length>data.length) {
										//copy the items from the end of old queue
										for(int i=0,j=tmp.length-data.length;i<data.length;i++,j++)
												data[i]=tmp[j];
										head=0;
										tail=head;
										size=data.length;
								}
								else {
										for(int i=0;i<tmp.length;i++)
												data[i]=tmp[i];
										head=0;
										tail=tmp.length;
										size=tmp.length;
								}
						}
						else {
								data=new long[capacity];
								head=0;
								tail=0;
						}
						this.capacity=capacity;
				}

				/**
				 * Append a new long to the queue. If the queue is full the item on the head of
				 * the queue is lost.
				 * @param val the long to append to the queue
				 */
				public synchronized void append(long val) {
						data[tail]=val;
						tail++;
						if(tail==capacity)
								tail=0;
						if(isFull()) {			
								//we were already loaded, so move the head also
								head++;
								if(head==capacity)
										head=0;
						}
						else {
								//only if we were not full already
								size++;
						}
				}
				
				/**
				 * Prepend a new long to the queue. If the queue is full the item on the tail of
				 * the queue is lost.
				 * @param val the long to prepend to the beginning of the queue
				 */
				public synchronized void prepend(long val) {
						if(isEmpty()) {
								//first item, just append it
								append(val);
								return;
						}
						head--;
						if(head<0)
								head=capacity-1;
						data[head]=val;
						if(isFull()) {
								//we were already loaded so move the tail of the queue also
								tail--;
								if(tail<0)
										tail=capacity-1;
						}
						else {
								size++;
						}
				}

				/**
				 * Pops the first item from the queue. The item is removed from the queue and returned.
				 * @return long the long at the head of the queue
				 * @throws IndexOutOfBoundsException if this method is called
				 * for an empty queue. 
				 */
				public synchronized long pop() {
						if(size==0)
								throw new IndexOutOfBoundsException("Queue is empty"); //$NON-NLS-1$
						long retval=data[head];
						head++;
						if(head==capacity)
								head=0;
						size--;
						return retval;
				}
						
				/**
				 * Gets the first item from the queue. The item is NOT removed from the queue.
				 * @return long the value of the long at the head of the queue
				 * @throws IndexOutOfBoundsException if this method is called
				 * for an empty queue. 
				 */
				public synchronized long peek() {
						if(size==0)
								throw new IndexOutOfBoundsException("Queue is empty"); //$NON-NLS-1$
						return data[head];
				}
						
				/**
				 * Gets an array with all the items in the queue.
				 * The array contains only the items that were apended or prepended
				 * to the queue. 
				 * @return long[] the array of items in the queue. The length of the 
				 * returned array will be the value that is returned by the size() method.
				 */
				public synchronized long[] toArray() {
						if(isEmpty())
								return new long[0];
						long[] retval=new long[size];
						int i=head;
						int s=0;
						while(s<size) {
								retval[s]=data[i];
								i++;
								if(i==capacity)
										i=0;
								s++;
						}
						return retval;
				}

				/**
				 * Gets a String representation of the queue
				 * @return String a String representation of the queue
				 */
				public String toString() {
						long [] tmp=toArray();
						StringBuffer sb=new StringBuffer();
						sb.append("ArrayQueue.Long Capacity=" //$NON-NLS-1$
											+String.valueOf(capacity())
											+" Size=" //$NON-NLS-1$
											+String.valueOf(size())
											+" Remaining=" //$NON-NLS-1$
											+String.valueOf(remaining())
											+" ["); //$NON-NLS-1$
						for(int i=0;i<tmp.length;i++)
								sb.append(String.valueOf(tmp[i])+","); //$NON-NLS-1$
						sb.append("]"); //$NON-NLS-1$
						return sb.toString();
				}
				
				/**
				 * The implementation of the Cloneable interface.
				 * @return Object a new ArrayQueue.Long that has the same
				 * capacity and items as the one for which it is called.
				 */
				public Object clone() {
						ArrayQueue.Long cl=new ArrayQueue.Long(capacity);
						//now copy the items, if any
						long [] tmp=toArray();
						for(int i=0;i<tmp.length;i++)
								cl.append(tmp[i]);
						return cl;
				}
		}

		/** 
		 * An ArrayQueue for floats. 
		 */
		public static class Float extends ArrayQueue implements Cloneable {
				/** our data */
				private float[] data;

				/**
				 * Creates a new ArrayQueue.Float. 
				 * @param capacity the capacity of the queue.
				 * The capacity must be at least 1 because we want to have at least 1 data-item to write our values to.
				 * @throws IllegalArgumentException if capacity<1
				 */
				public Float(int capacity) {
						super(capacity);
						data=new float[capacity];
				}

				/**
				 * Resize the queue. If the new capacity is larger or equal to the current 
				 * capacity of the queue all items in the queue will be copied to the beginning 
				 * of the new queue. If the new capacity is smaller than the current capacity 
				 * the method will copy items from the end of the queue. 
				 * Items at the end of the queue are assumed to be <em>newer</em> and therfore
				 * <em>more important</em> than the items from the beginning of the queue.
				 * @param capacity new capacity of the queue.  
				 * The capacity must be at least 1 because we want to have at least 1 data-item to write our values to.
				 * @throws IllegalArgumentException if capacity<1
				 */
				public synchronized void resize(int capacity) {
						if(capacity<1)
								throw new IllegalArgumentException("ArrayQueue must have a capacity > 0"); //$NON-NLS-1$
						if(size!=0) {
								float [] tmp=toArray();
								data=new float[capacity];
								if(tmp.length>data.length) {
										//copy the items from the end of old queue
										for(int i=0,j=tmp.length-data.length;i<data.length;i++,j++)
												data[i]=tmp[j];
										head=0;
										tail=head;
										size=data.length;
								}
								else {
										for(int i=0;i<tmp.length;i++)
												data[i]=tmp[i];
										head=0;
										tail=tmp.length;
										size=tmp.length;
								}
						}
						else {
								data=new float[capacity];
								head=0;
								tail=0;
						}
						this.capacity=capacity;
				}

				/**
				 * Append a new float to the queue. If the queue is full the item on the head of
				 * the queue is lost.
				 * @param val the float to append to the queue
				 */
				public synchronized void append(float val) {
						data[tail]=val;
						tail++;
						if(tail==capacity)
								tail=0;
						if(isFull()) {			
								//we were already loaded, so move the head also
								head++;
								if(head==capacity)
										head=0;
						}
						else {
								//only if we were not full already
								size++;
						}
				}
				
				/**
				 * Prepend a new float to the queue. If the queue is full the item on the tail of
				 * the queue is lost.
				 * @param val the float to prepend to the beginning of the queue
				 */
				public synchronized void prepend(float val) {
						if(isEmpty()) {
								//first item, just append it
								append(val);
								return;
						}
						head--;
						if(head<0)
								head=capacity-1;
						data[head]=val;
						if(isFull()) {
								//we were already loaded so move the tail of the queue also
								tail--;
								if(tail<0)
										tail=capacity-1;
						}
						else {
								size++;
						}
				}

				/**
				 * Pops the first item from the queue. The item is removed from the queue and returned.
				 * @return float the float at the head of the queue
				 * @throws IndexOutOfBoundsException if this method is called
				 * for an empty queue. 
				 */
				public synchronized float pop() {
						if(size==0)
								throw new IndexOutOfBoundsException("Queue is empty"); //$NON-NLS-1$
						float retval=data[head];
						head++;
						if(head==capacity)
								head=0;
						size--;
						return retval;
				}
						
				/**
				 * Gets the first item from the queue. The item is NOT removed from the queue.
				 * @return float the value of the float at the head of the queue
				 * @throws IndexOutOfBoundsException if this method is called
				 * for an empty queue. 
				 */
				public synchronized float peek() {
						if(size==0)
								throw new IndexOutOfBoundsException("Queue is empty"); //$NON-NLS-1$
						return data[head];
				}
						
				/**
				 * Gets an array with all the items in the queue.
				 * The array contains only the items that were apended or prepended
				 * to the queue. 
				 * @return float[] the array of items in the queue. The length of the 
				 * returned array will be the value that is returned by the size() method.
				 */
				public synchronized float[] toArray() {
						if(isEmpty())
								return new float[0];
						float[] retval=new float[size];
						int i=head;
						int s=0;
						while(s<size) {
								retval[s]=data[i];
								i++;
								if(i==capacity)
										i=0;
								s++;
						}
						return retval;
				}

				/**
				 * Gets a String representation of the queue
				 * @return String a String representation of the queue
				 */
				public String toString() {
						float [] tmp=toArray();
						StringBuffer sb=new StringBuffer();
						sb.append("ArrayQueue.Float Capacity=" //$NON-NLS-1$
											+String.valueOf(capacity())
											+" Size=" //$NON-NLS-1$
											+String.valueOf(size())
											+" Remaining=" //$NON-NLS-1$
											+String.valueOf(remaining())
											+" ["); //$NON-NLS-1$
						for(int i=0;i<tmp.length;i++)
								sb.append(String.valueOf(tmp[i])+","); //$NON-NLS-1$
						sb.append("]"); //$NON-NLS-1$
						return sb.toString();
				}
				
				/**
				 * The implementation of the Cloneable interface.
				 * @return Object a new ArrayQueue.Float that has the same
				 * capacity and items as the one for which it is called.
				 */
				public Object clone() {
						ArrayQueue.Float cl=new ArrayQueue.Float(capacity);
						//now copy the items, if any
						float [] tmp=toArray();
						for(int i=0;i<tmp.length;i++)
								cl.append(tmp[i]);
						return cl;
				}
		}

		/** 
		 * An ArrayQueue for doubles. 
		 */
		public static class Double extends ArrayQueue implements Cloneable {
				/** our data */
				private double[] data;

				/**
				 * Creates a new ArrayQueue.Double. 
				 * @param capacity the capacity of the queue.
				 * The capacity must be at least 1 because we want to have at least 1 data-item to write our values to.
				 * @throws IllegalArgumentException if capacity<1
				 */
				public Double(int capacity) {
						super(capacity);
						data=new double[capacity];
				}

				/**
				 * Resize the queue. If the new capacity is larger or equal to the current 
				 * capacity of the queue all items in the queue will be copied to the beginning 
				 * of the new queue. If the new capacity is smaller than the current capacity 
				 * the method will copy items from the end of the queue. 
				 * Items at the end of the queue are assumed to be <em>newer</em> and therfore
				 * <em>more important</em> than the items from the beginning of the queue.
				 * @param capacity new capacity of the queue.  
				 * The capacity must be at least 1 because we want to have at least 1 data-item to write our values to.
				 * @throws IllegalArgumentException if capacity<1
				 */
				public synchronized void resize(int capacity) {
						if(capacity<1)
								throw new IllegalArgumentException("ArrayQueue must have a capacity > 0"); //$NON-NLS-1$
						if(size!=0) {
								double [] tmp=toArray();
								data=new double[capacity];
								if(tmp.length>data.length) {
										//copy the items from the end of old queue
										for(int i=0,j=tmp.length-data.length;i<data.length;i++,j++)
												data[i]=tmp[j];
										head=0;
										tail=head;
										size=data.length;
								}
								else {
										for(int i=0;i<tmp.length;i++)
												data[i]=tmp[i];
										head=0;
										tail=tmp.length;
										size=tmp.length;
								}
						}
						else {
								data=new double[capacity];
								head=0;
								tail=0;
						}
						this.capacity=capacity;
				}

				/**
				 * Append a new double to the queue. If the queue is full the item on the head of
				 * the queue is lost.
				 * @param val the double to append to the queue
				 */
				public synchronized void append(double val) {
						data[tail]=val;
						tail++;
						if(tail==capacity)
								tail=0;
						if(isFull()) {			
								//we were already loaded, so move the head also
								head++;
								if(head==capacity)
										head=0;
						}
						else {
								//only if we were not full already
								size++;
						}
				}
				
				/**
				 * Prepend a new double to the queue. If the queue is full the item on the tail of
				 * the queue is lost.
				 * @param val the double to prepend to the beginning of the queue
				 */
				public synchronized void prepend(double val) {
						if(isEmpty()) {
								//first item, just append it
								append(val);
								return;
						}
						head--;
						if(head<0)
								head=capacity-1;
						data[head]=val;
						if(isFull()) {
								//we were already loaded so move the tail of the queue also
								tail--;
								if(tail<0)
										tail=capacity-1;
						}
						else {
								size++;
						}
				}

				/**
				 * Pops the first item from the queue. The item is removed from the queue and returned.
				 * @return double the double at the head of the queue
				 * @throws IndexOutOfBoundsException if this method is called
				 * for an empty queue. 
				 */
				public synchronized double pop() {
						if(size==0)
								throw new IndexOutOfBoundsException("Queue is empty"); //$NON-NLS-1$
						double retval=data[head];
						head++;
						if(head==capacity)
								head=0;
						size--;
						return retval;
				}
						
				/**
				 * Gets the first item from the queue. The item is NOT removed from the queue.
				 * @return double the value of the double at the head of the queue
				 * @throws IndexOutOfBoundsException if this method is called
				 * for an empty queue. 
				 */
				public synchronized double peek() {
						if(size==0)
								throw new IndexOutOfBoundsException("Queue is empty"); //$NON-NLS-1$
						return data[head];
				}
						
				/**
				 * Gets an array with all the items in the queue.
				 * The array contains only the items that were apended or prepended
				 * to the queue. 
				 * @return double[] the array of items in the queue. The length of the 
				 * returned array will be the value that is returned by the size() method.
				 */
				public synchronized double[] toArray() {
						if(isEmpty())
								return new double[0];
						double[] retval=new double[size];
						int i=head;
						int s=0;
						while(s<size) {
								retval[s]=data[i];
								i++;
								if(i==capacity)
										i=0;
								s++;
						}
						return retval;
				}

				/**
				 * Gets a String representation of the queue
				 * @return String a String representation of the queue
				 */
				public String toString() {
						double [] tmp=toArray();
						StringBuffer sb=new StringBuffer();
						sb.append("ArrayQueue.Double Capacity=" //$NON-NLS-1$
											+String.valueOf(capacity())
											+" Size=" //$NON-NLS-1$
											+String.valueOf(size())
											+" Remaining=" //$NON-NLS-1$
											+String.valueOf(remaining())
											+" ["); //$NON-NLS-1$
						for(int i=0;i<tmp.length;i++)
								sb.append(String.valueOf(tmp[i])+","); //$NON-NLS-1$
						sb.append("]"); //$NON-NLS-1$
						return sb.toString();
				}
				
				/**
				 * The implementation of the Cloneable interface.
				 * @return Object a new ArrayQueue.Double that has the same
				 * capacity and items as the one for which it is called.
				 */
				public Object clone() {
						ArrayQueue.Double cl=new ArrayQueue.Double(capacity);
						//now copy the items, if any
						double [] tmp=toArray();
						for(int i=0;i<tmp.length;i++)
								cl.append(tmp[i]);
						return cl;
				}
		}

		/** 
		 * An ArrayQueue for booleans. 
		 */
		public static class Boolean extends ArrayQueue implements Cloneable {
				/** our data */
				private boolean[] data;

				/**
				 * Creates a new ArrayQueue.Boolean. 
				 * @param capacity the capacity of the queue.
				 * The capacity must be at least 1 because we want to have at least 1 data-item to write our values to.
				 * @throws IllegalArgumentException if capacity<1
				 */
				public Boolean(int capacity) {
						super(capacity);
						data=new boolean[capacity];
				}

				/**
				 * Resize the queue. If the new capacity is larger or equal to the current 
				 * capacity of the queue all items in the queue will be copied to the beginning 
				 * of the new queue. If the new capacity is smaller than the current capacity 
				 * the method will copy items from the end of the queue. 
				 * Items at the end of the queue are assumed to be <em>newer</em> and therfore
				 * <em>more important</em> than the items from the beginning of the queue.
				 * @param capacity new capacity of the queue.  
				 * The capacity must be at least 1 because we want to have at least 1 data-item to write our values to.
				 * @throws IllegalArgumentException if capacity<1
				 */
				public synchronized void resize(int capacity) {
						if(capacity<1)
								throw new IllegalArgumentException("ArrayQueue must have a capacity > 0"); //$NON-NLS-1$
						if(size!=0) {
								boolean [] tmp=toArray();
								data=new boolean[capacity];
								if(tmp.length>data.length) {
										//copy the items from the end of old queue
										for(int i=0,j=tmp.length-data.length;i<data.length;i++,j++)
												data[i]=tmp[j];
										head=0;
										tail=head;
										size=data.length;
								}
								else {
										for(int i=0;i<tmp.length;i++)
												data[i]=tmp[i];
										head=0;
										tail=tmp.length;
										size=tmp.length;
								}
						}
						else {
								data=new boolean[capacity];
								head=0;
								tail=0;
						}
						this.capacity=capacity;
				}

				/**
				 * Append a new boolean to the queue. If the queue is full the item on the head of
				 * the queue is lost.
				 * @param val the boolean to append to the queue
				 */
				public synchronized void append(boolean val) {
						data[tail]=val;
						tail++;
						if(tail==capacity)
								tail=0;
						if(isFull()) {			
								//we were already loaded, so move the head also
								head++;
								if(head==capacity)
										head=0;
						}
						else {
								//only if we were not full already
								size++;
						}
				}
				
				/**
				 * Prepend a new boolean to the queue. If the queue is full the item on the tail of
				 * the queue is lost.
				 * @param val the boolean to prepend to the beginning of the queue
				 */
				public synchronized void prepend(boolean val) {
						if(isEmpty()) {
								//first item, just append it
								append(val);
								return;
						}
						head--;
						if(head<0)
								head=capacity-1;
						data[head]=val;
						if(isFull()) {
								//we were already loaded so move the tail of the queue also
								tail--;
								if(tail<0)
										tail=capacity-1;
						}
						else {
								size++;
						}
				}

				/**
				 * Pops the first item from the queue. The item is removed from the queue and returned.
				 * @return boolean the boolean at the head of the queue
				 * @throws IndexOutOfBoundsException if this method is called
				 * for an empty queue. 
				 */
				public synchronized boolean pop() {
						if(size==0)
								throw new IndexOutOfBoundsException("Queue is empty"); //$NON-NLS-1$
						boolean retval=data[head];
						head++;
						if(head==capacity)
								head=0;
						size--;
						return retval;
				}
						
				/**
				 * Gets the first item from the queue. The item is NOT removed from the queue.
				 * @return boolean the value of the boolean at the head of the queue
				 * @throws IndexOutOfBoundsException if this method is called
				 * for an empty queue. 
				 */
				public synchronized boolean peek() {
						if(size==0)
								throw new IndexOutOfBoundsException("Queue is empty"); //$NON-NLS-1$
						return data[head];
				}
						
				/**
				 * Gets an array with all the items in the queue.
				 * The array contains only the items that were apended or prepended
				 * to the queue. 
				 * @return boolean[] the array of items in the queue. The length of the 
				 * returned array will be the value that is returned by the size() method.
				 */
				public synchronized boolean[] toArray() {
						if(isEmpty())
								return new boolean[0];
						boolean[] retval=new boolean[size];
						int i=head;
						int s=0;
						while(s<size) {
								retval[s]=data[i];
								i++;
								if(i==capacity)
										i=0;
								s++;
						}
						return retval;
				}

				/**
				 * Gets a String representation of the queue
				 * @return String a String representation of the queue
				 */
				public String toString() {
						boolean [] tmp=toArray();
						StringBuffer sb=new StringBuffer();
						sb.append("ArrayQueue.Boolean Capacity=" //$NON-NLS-1$
											+String.valueOf(capacity())
											+" Size=" //$NON-NLS-1$
											+String.valueOf(size())
											+" Remaining=" //$NON-NLS-1$
											+String.valueOf(remaining())
											+" ["); //$NON-NLS-1$
						for(int i=0;i<tmp.length;i++)
								sb.append(String.valueOf(tmp[i])+","); //$NON-NLS-1$
						sb.append("]"); //$NON-NLS-1$
						return sb.toString();
				}
				
				/**
				 * The implementation of the Cloneable interface.
				 * @return Object a new ArrayQueue.Boolean that has the same
				 * capacity and items as the one for which it is called.
				 */
				public Object clone() {
						ArrayQueue.Boolean cl=new ArrayQueue.Boolean(capacity);
						//now copy the items, if any
						boolean [] tmp=toArray();
						for(int i=0;i<tmp.length;i++)
								cl.append(tmp[i]);
						return cl;
				}
		}

}


		
				
		


