public class Queue<V> {
    private ArrayList<V> array;

    public Queue(){
        array = new ArrayList<>();
    }

    public void enqueue(V v){
        if(v == null){
            throw new IllegalArgumentException();
        }
        array.add(v);
    }

    public V dequeue(){
        if(array.isEmpty()){
            throw new RuntimeException();
        }
        V v = array.get(0);
        array.remove(0);
        return v;
    }

    public V top(){
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
