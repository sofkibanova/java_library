public class Graph<V> {
    private ArrayList<V> vertices;
    private ArrayList<ArrayList<Edge<V>>> edges;
    private boolean hasDirections;

    public Graph() {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        hasDirections = true;
    }

    public Graph(boolean hasDirections) {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        this.hasDirections = hasDirections;
    }

    public void addVertex(V v) {
        if (!vertices.containsV(v)) {
            vertices.add(v);
            edges.add(new ArrayList<>());
        }
    }

    public void addEdge(V from, V to, long weight) {
        addVertex(from);
        addVertex(to);

        int indexFrom = findIndexOfVertex(from);
        edges.get(indexFrom).add(new Edge<V>(from, to, weight));

        if(!hasDirections){
            int indexTo = findIndexOfVertex(to);
            edges.get(indexTo).add(new Edge<V>(to, from, weight));
        }
    }

    public void removeVertex(V v){
        int indexOfVertex = findIndexOfVertex(v);
        if(indexOfVertex == -1){
            throw new RuntimeException();
        }
        for(int i = 0; i < edges.size(); i++){
            if(i != indexOfVertex){
                removeVertexEdges(i, v);
            }
        }
        vertices.remove(indexOfVertex);
        edges.remove(indexOfVertex);
    }

    public void removeEdge(V from, V to){
        int indexFrom = findIndexOfVertex(from);
        if(indexFrom != -1){
            removeVertexEdges(indexFrom, to);
        }
        if(!hasDirections){
            int indexTo = findIndexOfVertex(to);
            if(indexTo != -1){
                removeVertexEdges(indexTo, from);
            }
        }
    }

    public ArrayList<V> getNeighbours(V v){
        ArrayList<V> neighbours = new ArrayList<>();
        int index = findIndexOfVertex(v);

        if(index != -1){
            for(int i = 0; i < edges.get(index).size(); i++){
                neighbours.add(edges.get(index).get(i).destination);
            }
        }
        return neighbours;
    }

    public void bfs(V from){
        int indexFrom = findIndexOfVertex(from);
        if(indexFrom == -1){
            return;
        }

        ArrayList<Boolean> visitedVertex = new ArrayList<>();
        for(int i = 0; i < vertices.size(); i++){
            visitedVertex.add(false);
        }

        Queue<V> queue = new Queue<>();
        visitedVertex.set(indexFrom, true);
        queue.enqueue(from);

        System.out.print("BFS: ");

        while(!queue.isEmpty()){
            V current = queue.dequeue();
            System.out.print(current + " ");
            int currentIndex = findIndexOfVertex(current);

            for(int i = 0; i < edges.get(currentIndex).size(); i++){
                V neighbour = edges.get(currentIndex).get(i).destination;
                int neighbourIndex = findIndexOfVertex(neighbour);
                if(!visitedVertex.get(neighbourIndex)){
                    visitedVertex.set(neighbourIndex, true);
                    queue.enqueue(neighbour);
                }
            }
        }
        System.out.println();
    }

    public void dfs(V from){
        int indexFrom = findIndexOfVertex(from);
        if(indexFrom == -1){
            return;
        }

        ArrayList<Boolean> visitedVertex = new ArrayList<>();
        for(int i = 0; i < vertices.size(); i++){
            visitedVertex.add(false);
        }

        Stack<V> stack = new Stack<>();
        stack.push(from);

        System.out.print("DFS: ");

        while(!stack.isEmpty()){
            V current = stack.pop();
            System.out.print(current + " ");
            int currentIndex = findIndexOfVertex(current);

            if(!visitedVertex.get(currentIndex)){
                visitedVertex.set(currentIndex, true);
                System.out.print(current + " ");

                for(int i = edges.get(currentIndex).size() - 1; i >= 0; i--){
                    V neighbour = edges.get(currentIndex).get(i).destination;
                    int neighbourIndex = findIndexOfVertex(neighbour);
                    if(!visitedVertex.get(neighbourIndex)){
                        stack.push(neighbour);
                    }
                }
            }
        }
        System.out.println();
    }

    private int findIndexOfVertex(V v){
        for(int i = 0; i < vertices.size(); i++){
            if(v.equals(vertices.get(i))){
                return i;
            }
        }
        return -1;
    }

    private void removeVertexEdges(int index, V v){
        for(int i = edges.get(index).size() - 1; i >= 0; i--){
            if(edges.get(index).get(i).destination.equals(v)){
                edges.get(index).remove(i);
            }
        }
    }

    public boolean containsVertex(V v){
        return findIndexOfVertex(v) != -1;
    }

    public boolean containsEdge(V from, V to){
        int indexFrom = findIndexOfVertex(from);
        if(indexFrom == -1){
            return false;
        }
        for(int i = 0; i < edges.get(indexFrom).size(); i++){
            if(edges.get(indexFrom).get(i).destination.equals(to)){
                return true;
            }
        }
        return false;
    }

    public int countVertex(){
        return vertices.size();
    }

        public void printGraph(){
            for(int i = 0; i < vertices.size(); i++){
                System.out.print(vertices.get(i) + " ---> ");
                ArrayList<V> neighbour = getNeighbours(vertices.get(i));
                for (int j = 0; j < neighbour.size(); j++) {
                    System.out.print(neighbour.get(j) + " ");
                }
                System.out.println();
            }
        }
}
