package cmsc420.meeshquest.part1;

import java.util.ArrayList;
import org.w3c.dom.Element;

import org.w3c.dom.Document;

public class SGTree<T extends Comparable<T>> extends BinarySearchTree<T> {
	private Node<T> root;
	int n;
	int m;
	
	public SGTree() {
		super();
		root = null;
		n = 0;
		m = 0;
	}
	
	public boolean isEmpty() {
		return (this.root == null);
	}

	@Override
	public void printing(Document results, Element sgt) {
		printHelper(this.root, results, sgt);
	}
	private void printHelper(Node<T> r, Document results, Element sgt) {
		if (r != null) {
			City data = (City) r.getData();
			Element el;
			
			if (r.isExternal()) {
				el = results.createElement("external");
			}
			else {
				el = results.createElement("internal");
			}
			
			el.setAttribute("name", data.name);
			el.setAttribute("x", data.x);
			el.setAttribute("y", data.y);
			sgt.appendChild(el);
			printHelper(r.left, results, el);
			printHelper(r.right, results, el);
		}
	}
	
	@Override
	public void insert(T data) {
		ArrayList<Node<T>> path = new ArrayList<Node<T>>();
		this.root = this.insert(root, data, null, path);
		n++;
		m++;
		/*If new tree height exceeds log3/2(m):
		 - Walk back up the search path until we find the node u closest to the root such that
			2 * size(u) < 3 * size(u.child)
		 - Here size(u) is the number of nodes in u’s subtree and u. child is u’s child on search path
		 - A node on the insertion path satisfying this is called a candidate scapegoat
		 - Rebuild the subtree rooted at u
		*/
		if (root.getHeight() > Math.log(m)/Math.log(3.0/2.0)) {
			Node<T> scapegoat = null;
			int lastMove = -1;
			Node<T> last = root;
			for (int i = 0; i < path.size(); i++) {
				Node<T> pi = path.get(i);
				if (i > 0) {
					if (path.get(i-1).left == pi) {
						lastMove = 0;
						last = path.get(i-1);
					}
					else {
						lastMove = 1;
						last = path.get(i-1);
					}
				}
				if (pi.left.isExternal()) {
					if (2 * pi.getSize() < 3 * pi.right.getSize()) {
						scapegoat = pi;
						break;
					}
				}
				else if (pi.right.isExternal()) {
					if (2 * pi.getSize() < 3 * pi.left.getSize()) {
						scapegoat = pi;
						break;
					}
				}
				else if (pi.right.isExternal() && pi.left.isExternal()) {
					scapegoat = null;
				}
				else {
					if (2 * pi.getSize() < 3 * pi.left.getSize() || 2 * pi.getSize() < 3 * pi.right.getSize()) {
						scapegoat = pi;
						break;
					}
				}
			}
			ArrayList<T> temp = this.inOrderHelper(scapegoat);
			if (lastMove == -1) {
				this.root = buildSubtree(temp, 0, temp.size());
			}
			else {
				if (lastMove == 0 && scapegoat != null) {
					last.left = buildSubtree(temp, 0, temp.size());
				}
				else if (scapegoat != null){
					last.right = buildSubtree(temp, 0, temp.size());
				}
			}
		}
	}
	private Node<T> insert(Node<T> p, T toInsert, Node<T> last, ArrayList<Node<T>> path) {
		if (p == null) { 
			if (last != null) {
				Node<T> temp = new ExternalNode<T>(toInsert);
				T tempData = last.data;
				last = new InternalNode<T>((compareCities(toInsert, last.data) < 0) ? toInsert : last.data);
				if (compareCities(toInsert, tempData) < 0) {
					last.left = temp;
					last.right = new ExternalNode<T>(tempData);
				}
				else {
					last.right = temp;
					last.left = new ExternalNode<T>(tempData);
				}
				path.add(0, last);
				last.update();
				return last;
			}
			else {
				p = new ExternalNode<T>(toInsert);
			}
		}
		else if (compareCities(toInsert, p.data) == 0) {
			// duplicates
		}
		else if (compareCities(toInsert, p.data) < 0) {
			if (p.left == null) {
				p = insert(p.left, toInsert, p, path);
			}
			else {
				p.left = insert(p.left, toInsert, p, path);
			}
		}
		else {
			if (p.right == null) {
				p = insert(p.right, toInsert, p, path);
			}
			else {
				p.right = insert(p.right, toInsert, p, path);
			}
		}
		
		if (p != null) {
			p.update();
		}
		path.add(0, p);
		return p;
	}

	@Override
	public void delete(T toDelete) {
		if (compareCities(toDelete, root.getData()) == 0 && root.isExternal()) {
			this.root = null;
		}
		else {
			this.delete(this.root, toDelete, null);
		}
		n--;
		if (2*n < m) {
			ArrayList<T> temp = inOrderTraversal();
			this.root = buildSubtree(temp, 0, temp.size());
			m = n;
		}
	}
	private Node<T> delete(Node<T> p, T toDelete, Node<T> last) {
		if (p == null) {
			throw new RuntimeException("cannot delete.");
		}
		else if (compareCities(toDelete, p.data) < 0 || (compareCities(toDelete, p.data) == 0 && !p.isExternal())) {
			p.left = delete(p.left, toDelete, p);
		}
		else if (compareCities(toDelete, p.data) > 0) {
			p.right = delete(p.right, toDelete, p);
		}
		else {
			return null;
		}
		
		if (!p.isExternal() && (p.right == null || p.left == null)) {
			if (p.right == null) {
				p = p.left;
			}
			else {
				p = p.right;
			}
		}
		p.update();
		return p;
	}
	
	private Node<T> buildSubtree(ArrayList<T> data, int i, int k) {
		if (k == 1) {
			return new ExternalNode<T>(data.get(i));
		}
		else if (k == 2) {
			Node<T> p = new InternalNode<T>(data.get(i));
			p.left = new ExternalNode<T>(data.get(i));
			p.right = new ExternalNode<T>(data.get(i+1));
			return p;
		}
		else {
			int median = k/2;
			if (k%2 == 0) {
				Node<T> p = new InternalNode<T>(data.get(median + i - 1));
				p.left = buildSubtree(data, i, median);
				p.right = buildSubtree(data, i + median, k - median);
				return p;
			}
			else {
				Node<T> p = new InternalNode<T>(data.get(median + i));
				p.left = buildSubtree(data, i, median + 1);
				p.right = buildSubtree(data, i + median + 1, k - median - 1);
				return p;
			}
		}
	}

	@Override
	public ArrayList<T> inOrderTraversal() {
		return this.inOrderHelper(root);
	}
	private ArrayList<T> inOrderHelper(Node<T> r) {
		ArrayList<T> temp = new ArrayList<T>();

		if (r != null) {
			temp.addAll(inOrderHelper(r.left));
			if (r.isExternal()) {
				temp.add(r.getData());
			}
			temp.addAll(inOrderHelper(r.right));
		}

		return temp;
	}
	
	private int compareCities(T a, T b) {
		return ((City) a).compareToCoords((City) b);
	}
	
	private abstract class Node<G> {
		protected G data;
		protected Node<G> left, right;
		protected int size;
		protected int height;

		public Node(G data, Node<G> l, Node<G> r) {
			left = l;
			right = r;
			this.data = data;
		}

		public Node(G data) {
			this(data, null, null);
		}

		public G getData() {
			return data;
		}
		
		public abstract boolean isExternal();
		public abstract int getSize();
		public abstract int getHeight();
		protected abstract void update();
	}
	
	private class InternalNode<G> extends Node<G> {
		public InternalNode(G data) {
			super(data);
			super.size = 0;
		}
		
		public InternalNode(G data, Node<G> l, Node<G> r) {
			super(data, l, r);
			super.size = l.getSize() + r.getSize();
			super.height = 1 + (l.getHeight() > r.getHeight() ? l.getHeight() : r.getHeight());
		}
		
		@Override
		public boolean isExternal() {
			return false;
		}

		@Override
		public int getSize() {
			this.update();
			return super.size;
		}

		@Override
		public int getHeight() {
			this.update();
			return super.height;
		}
		
		public void update() {
			Node<G> l = super.left;
			Node<G> r = super.right;
			super.size = ((l != null) ? l.getSize() : 0) + ((r != null) ? r.getSize() : 0);
			if (l != null && r != null) {
				super.height = 1 + (l.getHeight() > r.getHeight() ? l.getHeight() : r.getHeight());
			}
			else {
				super.height = 0;
			}
		}
	}
	
	private class ExternalNode<G> extends Node<G> {
		public ExternalNode(G data) {
			super(data);
			super.size = 1;
			super.height = 0;
		}
		
		@Override
		public boolean isExternal() {
			return true;
		}

		@Override
		public int getSize() {
			return 1;
		}

		@Override
		public int getHeight() {
			return 0;
		}

		@Override
		protected void update() {
			// does nothing for external node
		}
	}
}
