/*
 * Copyright 2011 Sacaldur
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.rhocraft.rhotek.plugins.blockanimationplugin;

/**
 * 
 * @author Sacaldur
 *
 */

public class BlockID {
	public static final BlockID EMPTY = new BlockID(0, (byte)0);
	
	private int id;
	private byte data;

	public BlockID(int id, byte data) {
		this.id = id;
		this.data = data;
	}
	
	public BlockID(String value) throws NumberFormatException {
		if(value.matches("\\d+(.\\d+)?")) {
			int id = 0;
			byte data = 0;
			if(value.matches("\\d+")) {
				id = Integer.parseInt(value);
			} else {
				id = Integer.parseInt(value.replaceFirst(".\\d+", ""));
				data = Byte.parseByte(value.replaceFirst("\\d+.", ""));
			}
			this.data = data;
			this.id = id;
		}
		throw new NumberFormatException();
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the data value (eg the wool color)
	 */
	public byte getData() {
		return data;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof BlockID) {
			BlockID other = (BlockID) obj;
			if(other.id == this.id && other.data == this.data) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return this.id + "." + this.data;
	}
}
