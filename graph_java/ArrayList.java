public class ArrayList<V> {
    private int size;
    private Object[] array;
    private static final int ORIGINAL_CAPACITY = 10;

    public ArrayList(){
        array = new Object[ORIGINAL_CAPACITY];
        size = 0;
    }

    public int size() {return size;}
    public boolean isEmpty(){return size == 0;}

    @SuppressWarnings("unchecked")
    public V get(int index){
        if(index < 0 || index >= size){
            throw new IndexOutOfBoundsException();
        }
        return (V) array[index];
    }

    public void set(int index, V v){
        if(index < 0 || index >= size){
            throw new IndexOutOfBoundsException();
        }
        array[index] = v;
    }

    public void add(V element){
        if(size == array.length){
            resize();
        }
        array[size++] = element;
    }

    public void remove(int index){
        if(index < 0 || index >= size){
            return;
        }
        for(int i = index; i < size - 1; i ++){
            array[i] = array[i+1];
        }
        array[--size] = null;
    }

    public boolean remove(V v){
        for(int i = 0; i < size; i++){
            if(array[i].equals(v)){
                remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean containsV(V v){
        for(int i = 0; i < size; i++){
            if(array[i].equals(v)){
                return true;
            }
        }
        return false;
    }

    public void cleanArray(){
        for(int i = 0; i < size + 1; i++){
            array[i] = null;
        }
        size = 0;
    }

    private void resize(){
        Object[] newArray = new Object[array.length * 2];
        for( int i = 0; i < size; i++){
            newArray[i] = array[i];
        }
        array = newArray;
    }
}
