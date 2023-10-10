package com.example.tombstonetussle;

import java.util.*;

public class NPCCharacter {

    int x = 10; // Replace with your desired X-coordinate
    int y = 20; // Replace with your desired Y-coordinate

    private Queue<Node> path;

    public NPCCharacter(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.path = new LinkedList<>();
    }

    public class Node {
        private int x;
        private int y;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setPath(List<Node> newPath) {
        this.path.clear();
        this.path.addAll(newPath);
    }

    public void updatePosition(GameAreaModel playerModel, char[][] maze) {
        if (!path.isEmpty()) {
            Node nextNode = path.poll();
            this.setX(nextNode.getX());
            this.setY(nextNode.getY());
        } else {
            // Calculate a new path to follow the player character
            List<Node> newPath = calculatePathToPlayer(playerModel, maze);
            setPath(newPath);
        }
    }

    public List<Node> calculatePathToPlayer(GameAreaModel playerModel, char[][] maze) {
        int playerX = playerModel.getX();
        int playerY = playerModel.getY();
        int npcX = getX();
        int npcY = getY();

        // If the NPC is already at the player's position, return an empty path
        if (playerX == npcX && playerY == npcY) {
            return new ArrayList<>();
        }

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Up, Down, Left, Right

        boolean[][] visited = new boolean[maze.length][maze[0].length];
        Node[][] parent = new Node[maze.length][maze[0].length];

        Queue<Node> queue = new LinkedList<>();
        Node startNode = new Node(npcX, npcY);
        visited[npcX][npcY] = true;
        queue.add(startNode);

        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();

            for (int[] direction : directions) {
                int newX = currentNode.getX() + direction[0];
                int newY = currentNode.getY() + direction[1];

                if (newX >= 0 && newX < maze.length && newY >= 0 && newY < maze[0].length
                        && maze[newX][newY] != '#' && !visited[newX][newY]) {
                    Node newNode = new Node(newX, newY);
                    visited[newX][newY] = true;
                    parent[newX][newY] = currentNode;

                    if (newX == playerX && newY == playerY) {
                        // Found the player, backtrack to construct the path
                        List<Node> path = new ArrayList<>();
                        Node node = newNode;
                        while (node != null) {
                            path.add(node);
                            node = parent[node.getX()][node.getY()];
                        }
                        Collections.reverse(path);
                        path.remove(0); // Remove the NPC's current position from the path
                        return path;
                    }

                    queue.add(newNode);
                }
            }
        }

        // If no path is found, return an empty list (NPC stays in place)
        return new ArrayList<>();
    }
}
