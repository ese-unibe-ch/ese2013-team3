package ch.unibe.scg.team3.path;

import java.util.Iterator;
import java.util.LinkedList;

import ch.unibe.scg.team3.board.Point;
import ch.unibe.scg.team3.game.IElement;

/**
 * The responsibility of the ch.unibe.scg.team3.path is to know if he is valid
 * or not. A valid path is a path where its elements are adjacent. This class
 * partly wraps the {@link LinkedList}.
 * 
 * @author adrian
 */

public class Path<E extends IElement> implements Iterable<E> {

	private LinkedList<E> path;

	public Path() {
		path = new LinkedList<E>();
	}

	/**
	 * @param element
	 *            The element to be added, not null
	 * @return True, if the element is not already in the path and adjacent to
	 *         the last added element, false otherwise
	 */
	public boolean add(E element) {

		if (path.contains(element))
			return false;

		if (0 < length() && !isAdjacent(element, path.getLast()))
			return false;

		path.add(element);
		return true;
	}

	public void clear() {
		path.clear();
	}

	/**
	 * @return The length of the path is greater or equal to zero.
	 */
	public int length() {
		return path.size();
	}

	@Override
	public Iterator<E> iterator() {
		return path.iterator();
	}

	private boolean isAdjacent(E e1, E e2) {

		int b1_x = e1.getCoordinates().getX();
		int b1_y = e1.getCoordinates().getY();
		int b2_x = e2.getCoordinates().getX();
		int b2_y = e2.getCoordinates().getY();

		if ((Math.abs(b1_x - b2_x) > 1) || Math.abs(b1_y - b2_y) > 1) {
			return false;
		}
		return true;
	}

	public boolean isEmpty() {
		return path.isEmpty();
	}

	/**
	 * @return True, if there exists an element in the path with the same
	 *         coordinates and false otherwise.
	 */
	public boolean contains(E element) {

		for (E pathElement : path) {
			Point pCoord = pathElement.getCoordinates();
			if (pCoord.equals(element.getCoordinates()))
				return true;
		}

		return false;
	}

	/**
	 * Returns the last path element that was added
	 * 
	 * @return The last element if the path is not empty
	 */
	public E getLast() {
		return path.getLast();
	}

	/**
	 * Removes and returns the last path element that was added
	 * 
	 * @return The returned element if the path is not empty
	 */
	public E removeLast() {
		return path.removeLast();
	}

}
