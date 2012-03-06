package net.rhocraft.rhotek.plugins.blockanimationplugin;

public class BlockLocation {
	private int x;
	private int y;
	private int z;

	public BlockLocation(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @return the z
	 */
	public int getZ() {
		return z;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null) {
			if (obj instanceof BlockLocation) {
				BlockLocation other = (BlockLocation) obj;
				return this.equals(other.x, other.y, other.z);
			}
		}
		return super.equals(obj);
	}

	public boolean equals(int x, int y, int z) {
		return x == this.x && y == this.y && z == this.z;
	}
}
