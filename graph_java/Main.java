public class Main {
    public static void main(String[] args) {
        Graph<Integer> graph = new Graph<>(true);

        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4);
        graph.addVertex(5);

        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 1);
        graph.addEdge(4, 1, 1);
        graph.addEdge(1, 5, 1);

        System.out.println("Стуктура графа:");
        graph.printGraph();

        graph.dfs(1);
        graph.bfs(1);

        ArrayList<Integer> neighbour = graph.getNeighbours(1);
        System.out.print("\nВершины смежные с 1: ");
        for (int i = 0; i < neighbour.size(); i++) {
            System.out.print(neighbour.get(i) + " ");
        }
        System.out.println();


        System.out.println("Есть вершина 1: " + graph.containsVertex(1));
        System.out.println("Есть ребро 1 ---> 2: " + graph.containsEdge(1, 2));
        System.out.println("Есть ребро 1 ---> 3: " + graph.containsEdge(1, 3));

        System.out.println("\nУдаление ребра 1 ---> 2");
        graph.removeEdge(1, 2);
        graph.printGraph();

        System.out.println("Проверка: есть ли ребро 1 ---> 2: " + graph.containsEdge(1, 2));

        graph.dfs(1);
        graph.bfs(1);

        System.out.println("\nУдаляем вершину 3");
        graph.removeVertex(3);
        graph.printGraph();

        System.out.println("Количество вершин: " + graph.countVertex());

        System.out.println("\nРассмотрим ориентированный граф:");
        Graph<String> directedGraph = new Graph<>(true);

        directedGraph.addEdge("A", "B", 1);
        directedGraph.addEdge("B", "C", 1);
        directedGraph.addEdge("C", "A", 1);

        directedGraph.printGraph();
        directedGraph.dfs("A");
        directedGraph.bfs("A");
    }
}
