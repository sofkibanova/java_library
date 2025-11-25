public class Edge<V> {
    V source;
    V destination;
    long weight;

    public Edge(V from, V where, long weight) {
        this.source = from;
        this.destination = where;
        this.weight = weight;
    }

    public boolean equals(Edge<V> v){
        if(this == v){
            return true;
        }
        if(v == null || getClass() != v.getClass()){
            return false;
        }
        return source.equals(v.source) && destination.equals(v.destination);
    }
}
