package cmsc420.meeshquest.part1;

/** **************************************************************************
 *                     The  generic Binary Search Tree class.
 *
 * V.S.Adamchik 2010
 * Source: https://www.cs.cmu.edu/~adamchik/15-121/lectures/Trees/code/BST.java
 * mod. 2019 - Zain Bhaila
 *****************************************************************************/

import java.util.*;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

public class BinarySearchTree <T extends Comparable<T>> implements Iterable<T>
{
   private Node<T> root;
   private Comparator<T> comparator;

   public BinarySearchTree()
   {
      root = null;
      comparator = null;
   }

   public BinarySearchTree(Comparator<T> comp)
   {
      root = null;
      comparator = comp;
   }

   private int compare(T x, T y)
   {
      if(comparator == null) return x.compareTo(y);
      else
      return comparator.compare(x,y);
   }
   
   public T getRootData() {
	   if (root != null) {	   
		   return this.root.getData();
	   }
	   else {
		   return null;
	   }
   }
   public BinarySearchTree<T> getLeftSubtree() {
	   BinarySearchTree<T> temp = new BinarySearchTree<T>();
	   temp.root = this.root.left;
	   return temp;
   }
   public BinarySearchTree<T> getRightSubtree() {
	   BinarySearchTree<T> temp = new BinarySearchTree<T>();
	   temp.root = this.root.right;
	   return temp;
   }
   
   public void printing(Document results, Element bst) {
	   printHelper(root, results, bst);
   }
   private void printHelper(Node<T> r, Document results, Element bst)
   {
      if (r != null)
      {
    	 City data = (City) r.getData();
    	 Element el = results.createElement("node");
    	 el.setAttribute("name", data.name);
    	 el.setAttribute("x", data.x);
    	 el.setAttribute("y", data.y);
    	 bst.appendChild(el);
    	 printHelper(r.left, results, el);
    	 printHelper(r.right, results, el);
      }
   }

/*****************************************************
*
*            INSERT
*
******************************************************/
   public void insert(T data)
   {
      root = insert(root, data);
   }
   private Node<T> insert(Node<T> p, T toInsert)
   {
      if (p == null)
         return new Node<T>(toInsert);

      if (compare(toInsert, p.data) == 0)
      	return p;

      if (compare(toInsert, p.data) < 0)
         p.left = insert(p.left, toInsert);
      else
         p.right = insert(p.right, toInsert);

      return p;
   }

/*****************************************************
*
*            SEARCH
*
******************************************************/
   public boolean search(T toSearch)
   {
      return search(root, toSearch);
   }
   private boolean search(Node<T> p, T toSearch)
   {
      if (p == null)
         return false;
      else
      if (compare(toSearch, p.data) == 0)
      	return true;
      else
      if (compare(toSearch, p.data) < 0)
         return search(p.left, toSearch);
      else
         return search(p.right, toSearch);
   }

/*****************************************************
*
*            DELETE
*
******************************************************/

   public void delete(T toDelete)
   {
      root = delete(root, toDelete);
   }
   private Node<T> delete(Node<T> p, T toDelete)
   {
      if (p == null)  throw new RuntimeException("cannot delete.");
      else
      if (compare(toDelete, p.data) < 0)
      p.left = delete (p.left, toDelete);
      else
      if (compare(toDelete, p.data)  > 0)
      p.right = delete (p.right, toDelete);
      else
      {
         if (p.left == null) return p.right;
         else
         if (p.right == null) return p.left;
         else
         {
         // get data from the leftmost node in the right subtree
            p.data = retrieveData(p.right);
         // delete the leftmost node in the right subtree
            p.left =  delete(p.right, p.data) ;
         }
      }
      return p;
   }
   private T retrieveData(Node<T> p)
   {
      while (p.left != null) p = p.left;

      return p.data;
   }

/*************************************************
 *
 *            toString
 *
 **************************************************/

   public String toString()
   {
      StringBuffer sb = new StringBuffer();
      for(T data : this) sb.append(data.toString());

      return sb.toString();
   }

/*************************************************
 *
 *            TRAVERSAL
 *
 **************************************************/

   public ArrayList<T> preOrderTraversal()
   {
      return preOrderHelper(root);
   }
   private ArrayList<T> preOrderHelper(Node<T> r)
   {
	  ArrayList<T> temp = new ArrayList<T>();
		  
      if (r != null)
      {
          temp.add(r.getData());
          temp.addAll(preOrderHelper(r.left));
          temp.addAll(preOrderHelper(r.right));
      }
      
      return temp;
   }

   public ArrayList<T> inOrderTraversal()
   {
      return inOrderHelper(root);
   }
   private ArrayList<T> inOrderHelper(Node<T> r)
   {
	  ArrayList<T> temp = new ArrayList<T>();
	  
      if (r != null)
      {
         temp.addAll(inOrderHelper(r.left));
         temp.add(r.getData());
         temp.addAll(inOrderHelper(r.right));
      }
      
      return temp;
   }

/*************************************************
 *
 *            CLONE
 *
 **************************************************/

   public BinarySearchTree<T> clone()
   {
	   BinarySearchTree<T> twin = null;

      if(comparator == null)
         twin = new BinarySearchTree<T>();
      else
         twin = new BinarySearchTree<T>(comparator);

      twin.root = cloneHelper(root);
      return twin;
   }
   private Node<T> cloneHelper(Node<T> p)
   {
      if(p == null)
         return null;
      else
         return new Node<T>(p.data, cloneHelper(p.left), cloneHelper(p.right));
   }

/*************************************************
 *
 *            MISC
 *
 **************************************************/

   public int height()
   {
      return height(root);
   }
   private int height(Node<T> p)
   {
      if(p == null) return -1;
      else
      return 1 + Math.max( height(p.left), height(p.right));
   }

   public int countLeaves()
   {
      return countLeaves(root);
   }
   private int countLeaves(Node<T> p)
   {
      if(p == null) return 0;
      else
      if(p.left == null && p.right == null) return 1;
      else
      return countLeaves(p.left) + countLeaves(p.right);
   }



  //This method restores a BST given preorder and inorder traversals
   public void restore(T[] pre, T[] in)
   {
      root = restore(pre, 0, pre.length-1, in, 0, in.length-1);
   }
   private Node<T> restore(T[] pre, int preL, int preR, T[] in, int inL, int inR)
   {
      if(preL <= preR)
      {
         int count = 0;
         //find the root in the inorder array
         while(pre[preL] != in[inL + count]) count++;

         Node<T> tmp = new Node<T>(pre[preL]);
         tmp.left = restore(pre, preL+1, preL + count, in, inL, inL +count-1);
         tmp.right = restore(pre, preL+count+1, preR, in, inL+count+1, inR);
         return tmp;
      }
      else
         return null;
   }


   //The width of a binary tree is the maximum number of elements on one level of the tree.
   public int width()
   {
      int max = 0;
      for(int k = 0; k <= height(); k++)
      {
         int tmp = width(root, k);
         if(tmp > max) max = tmp;
      }
      return max;
   }
   //rerturns the number of node on a given level
   public int width(Node<T> p, int depth)
   {
      if(p==null) return 0;
      else
      if(depth == 0) return 1;
      else
      return width(p.left, depth-1) + width(p.right, depth-1);
   }

   //The diameter of a tree is the number of nodes
   //on the longest path between two leaves in the tree.
   public int diameter()
   {
      return diameter(root);
   }
   private int diameter(Node<T> p)
   {
      if(p==null) return 0;

      //the path goes through the root
      int len1 = height(p.left) + height(p.right) +3;

      //the path does not pass the root
      int len2 = Math.max(diameter(p.left), diameter(p.right));

      return Math.max(len1, len2);
   }


/*****************************************************
*
*            TREE ITERATOR
*
******************************************************/

   public Iterator<T> iterator()
   {
      return new MyIterator();
   }
   //pre-order
   private class MyIterator implements Iterator<T>
   {
      Stack<Node<T>> stk = new Stack<Node<T>>();

      public MyIterator()
      {
         if(root != null) stk.push(root);
      }
      public boolean hasNext()
      {
         return !stk.isEmpty();
      }

      public T next()
      {
         Node<T> cur = stk.peek();
         if(cur.left != null)
         {
            stk.push(cur.left);
         }
         else
         {
            Node<T> tmp = stk.pop();
            while( tmp.right == null )
            {
               if(stk.isEmpty()) return cur.data;
               tmp = stk.pop();
            }
            stk.push(tmp.right);
         }

         return cur.data;
      }//end of next()

      public void remove()
      {

      }
   }//end of MyIterator

/*****************************************************
*
*            the Node class
*
******************************************************/

   private class Node<G>
   {
      private G data;
      private Node<G> left, right;

      public Node(G data, Node<G> l, Node<G> r)
      {
         left = l; right = r;
         this.data = data;
      }

      public Node(G data)
      {
         this(data, null, null);
      }
      
      public G getData() {
    	  return data;
      }

      public String toString()
      {
         return data.toString();
      }
   } //end of Node
}//end of BST