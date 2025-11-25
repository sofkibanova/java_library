public class Stack<V>  {
    private ArrayList<V> array;

    public Stack(){
        array = new ArrayList<>();
    }

    public void push(V v){
        if(v == null){
            throw new IllegalArgumentException();
        }
        array.add(v);
    }

    public V pop(){
        if(array.isEmpty()){
            throw new RuntimeException();
        }
        V element = array.get(array.size() - 1);
        array.remove(array.size() - 1);
        return element;
    }

    public V peek(){
        if(array.isEmpty()){
            throw new RuntimeException();
        }
        return array.get(array.size() - 1);
    }

    public boolean isEmpty(){
        return array.isEmpty();
    }

    public int size(){
        return array.size();
    }
}
