type Child<T> = Option<Box<Node<T>>>;

struct Node<T> {
    value: T,
    left: Child<T>,
    right: Child<T>,
}

struct Bst<T> {
    root: Child<T>,
}

impl<T: Ord> Bst<T> {
    fn new() -> Self {
        Bst { root: None }
    }

    fn insert(&mut self, value: T) {
        Self::insert_at(&mut self.root, value);
    }

    fn insert_at(link: &mut Child<T>, value: T) {
        match link {
            None => {
                *link = Some(Box::new(Node {
                    value,
                    left: None,
                    right: None,
                }));
            }
            Some(node) => {
                if value < node.value {
                    Self::insert_at(&mut node.left, value);
                } else if value > node.value {
                    Self::insert_at(&mut node.right, value);
                }
                // equal values are ignored (no duplicates)
            }
        }
    }

    fn contains(&self, value: &T) -> bool {
        Self::search(&self.root, value)
    }

    fn search(link: &Child<T>, value: &T) -> bool {
        match link {
            None => false,
            Some(node) => {
                if *value < node.value {
                    Self::search(&node.left, value)
                } else if *value > node.value {
                    Self::search(&node.right, value)
                } else {
                    true
                }
            }
        }
    }

    fn in_order(&self) -> Vec<&T> {
        let mut result = Vec::new();
        Self::in_order_walk(&self.root, &mut result);
        result
    }

    fn in_order_walk<'a>(link: &'a Child<T>, result: &mut Vec<&'a T>) {
        if let Some(node) = link {
            Self::in_order_walk(&node.left, result); // left
            result.push(&node.value); // root
            Self::in_order_walk(&node.right, result); // right
        }
    }

    fn pre_order(&self) -> Vec<&T> {
        let mut result = Vec::new();
        Self::pre_order_walk(&self.root, &mut result);
        result
    }

    fn pre_order_walk<'a>(link: &'a Child<T>, result: &mut Vec<&'a T>) {
        if let Some(node) = link {
            result.push(&node.value);           // root
            Self::pre_order_walk(&node.left, result);   // left
            Self::pre_order_walk(&node.right, result);  // right
        }
    }
}

fn main() {
    let mut tree = Bst::new();

    for val in [5, 3, 7, 1, 4, 6, 8, 2] {
        tree.insert(val);
    }

    // A Traversal is the process of visiting every node in a tree in a specific order.
    // Since a tree isn't linear like an array, there are different strategies for walking through it.
    // For BST, there are 3 main depth-first traversals:
    // In-order (left -> root -> right) gives sorted output
    println!("in-order traversal: {:?}", tree.in_order());
    // Pre-order (root -> left -> right) useful for copying/serializing
    println!("pre-order traversal: {:?}", tree.pre_order());
    println!("contains 4? {}", tree.contains(&4));
    println!("contains 9? {}", tree.contains(&9));
}
