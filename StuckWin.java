/******************************************************************************
 * Compilation: javac Stuckwin.java
 * Execution: java Stuckwin
 * Dependencies: StdDraw.java
 * Authors: Zekeriya AKBURAK AND Ambre MAJOUREL
 * 
 * 
 * Stuckwin game with a graphical interface and a console interface
 * At the beginning of the game,
 * the player chooses the interface he wants to use (console(0) or graphical(1))
 * 
 * Remark:
 * by default, the game is played with player vs player (0) 
 * To play with IA , you have to choose the mode IA ( player vs player (0) score(1) or random(2))
 * exemple : java Stuckwin 1
 * 
 * 
 * 
 ******************************************************************************/
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

import java.awt.Color;
import java.awt.Font;
public class StuckWin {
    static final Scanner input = new Scanner(System.in);
    private static final double BOARD_SIZE = 7;

    enum Result {OK, BAD_COLOR, DEST_NOT_FREE, EMPTY_SRC, TOO_FAR, EXT_BOARD, EXIT}

    enum ModeMvt {REAL, SIMU}
    enum Affichage{CONSOLE, GUI}
    enum typeIA{HUMAIN ,SCORE,RANDOM}


    final char[] joueurs = {'B', 'R'};
    static final int SIZE = 8;
    static final char VIDE = '.';
    static final double SIZE_POLY = 0.5;
    static final double RAYON_POW2 = Math.pow(SIZE_POLY * 0.87, 2);
    static final double WITDH = 2.0 * SIZE_POLY;
    static final double HEIGHT = Math.sqrt(3) * SIZE_POLY;
    double[][][] pointCentre;
    Affichage modeAffichage = Affichage.CONSOLE;
    typeIA modeIA = typeIA.SCORE;
    static final String WHITE_BACKGROUND = "\033[47m"; // WHITE
    static final String RED_BACKGROUND = "\033[41m";   // RED
    static final String BLUE_BACKGROUND = "\033[44m";  // BLUE
    static final String RESET = "\033[0m";  // Text Reset
    Random rand = new Random();



    // 'B'=bleu 'R'=rouge '.'=vide '-'=n'existe pas


    char[][] state = {
            {'-', '-', '-', '-', 'R', 'R', 'R', 'R'},
            {'-', '-', '-', '.', 'R', 'R', 'R', 'R'},
            {'-', '-', '.', '.', '.', 'R', 'R', 'R'},
            {'-', 'B', 'B', '.', '.', '.', 'R', 'R'},
            {'-', 'B', 'B', 'B', '.', '.', '.', '-'},
            {'-', 'B', 'B', 'B', 'B', '.', '-', '-'},
            {'-', 'B', 'B', 'B', 'B', '-', '-', '-'},
    };

    /**
     * Vérifie si la case est vide
     *
     * @param lc case Lc
     * @return true si la case est vide
     */
    boolean isVide(String lc) {
        return "".equals(lc) || Objects.equals(lc, "" + VIDE);
    }

    /**
     * Vérifie si le déplacement est trop loin pour un pion et verifie si le pion ne recule pas
     *
     * @param couleur couleur du pion à déplacer
     * @param src     case source
     * @param dest    case destination
     * @return true si le déplacement est trop loin ou si le pion recule
     */
    boolean isTooFar(char couleur, int[] src, int[] dest) {
        if (Math.abs(src[0] - dest[0]) > 1 || Math.abs(src[1] - dest[1]) > 1) return true;
        if (couleur == 'B' && (src[0] - dest[0] == -1 || src[1] - dest[1] == 1)) return true;
        return (couleur == 'R' && (src[0] - dest[0] == 1 || src[1] - dest[1] == -1));
    }


    
    /**
     * Déplace un pion ou simule son déplacement
     *
     * @param couleur  couleur du pion à déplacer
     * @param lcSource case source Lc
     * @param lcDest   case destination Lc
     * @param mode     ModeMVT.REAL/SIMU selon qu'on réalise effectivement le déplacement ou qu'on le simule seulement.
     * @return enum {OK, BAD_COLOR, DEST_NOT_FREE, EMPTY_SRC, TOO_FAR, EXT_BOARD, EXIT} selon le déplacement
     */
    Result deplace(char couleur, String lcSource, String lcDest, ModeMvt mode) {
        if (isVide(lcSource) || isVide(lcDest)) return Result.EMPTY_SRC;
        int[] src = lcToCoord(lcSource);
        int[] dest = lcToCoord(lcDest);
        if (src.length == 0 || dest.length == 0) return Result.EMPTY_SRC;
        if (state[src[0]][src[1]] == VIDE) return Result.EMPTY_SRC;
        if (src[0] > BOARD_SIZE || src[1] >= SIZE || src[1] < 0) return Result.EXT_BOARD;
        if (dest[0] > BOARD_SIZE || dest[1] >= SIZE || dest[1] < 0) return Result.EXT_BOARD;
        if (state[dest[0]][dest[1]] == '-' || state[src[0]][src[1]] == '-') return Result.EXT_BOARD;
        if (state[dest[0]][dest[1]] != VIDE) return Result.DEST_NOT_FREE;
        if (state[src[0]][src[1]] != couleur) return Result.BAD_COLOR;
        if (isTooFar(couleur, src, dest)) return Result.TOO_FAR;
        if (mode == ModeMvt.REAL) {
            state[dest[0]][dest[1]] = couleur;
            state[src[0]][src[1]] = VIDE;
        }


        return Result.OK;
    }


    /**
     * Convertit une case Lc en coordonnées
     *
     * @param lc case Lc
     * @return tableau de 2 entiers {ligne, colonne}
     */
    int[] lcToCoord(String lc) {
        int[] coord = new int[2];
        if (lc.length() != 2) return new int[0];
        switch (lc.charAt(0)) {
            case 'A':
                coord[0] = 0;
                break;
            case 'B':
                coord[0] = 1;
                break;
            case 'C':
                coord[0] = 2;
                break;
            case 'D':
                coord[0] = 3;
                break;
            case 'E':
                coord[0] = 4;
                break;
            case 'F':
                coord[0] = 5;
                break;
            case 'G':
                coord[0] = 6;
                break;
            default:
                return new int[0];
        }
        try {
            coord[1] = Integer.parseInt(lc.charAt(1) + "");
            if (coord[1] >= SIZE || coord[1] < 1) return new int[0];
        } catch (NumberFormatException numFoEx) {
            return new int[0];
        }

        return coord;
    }


    /**
     * Construit les trois chaînes représentant les positions accessibles
     * à partir de la position de départ [idLettre][idCol].
     *
     * @param couleur  couleur du pion à jouer
     * @param idLettre id de la ligne du pion à jouer
     * @param idCol    id de la colonne du pion à jouer
     * @return tableau des trois positions jouables par le pion (redondance possible sur les bords)
     */
    String[] possibleDests(char couleur, int idLettre, int idCol) {
        String[] dests = new String[]{"", "", ""};
        if (couleur == 'B') {
            dests[0] = coordToLc(new int[]{idLettre - 1, idCol + 1});
            dests[1] = coordToLc(new int[]{idLettre - 1, idCol});
            dests[2] = coordToLc(new int[]{idLettre, idCol + 1});
        } else if (couleur == 'R') {
            dests[0] = coordToLc(new int[]{idLettre + 1, idCol - 1});
            dests[1] = coordToLc(new int[]{idLettre + 1, idCol});
            dests[2] = coordToLc(new int[]{idLettre, idCol - 1});

        }
        return dests;
    }

    /**
     * Convertit une coordonnée en case Lc
     *
     * @param coord tableau de 2 entiers {ligne, colonne}
     * @return case Lc
     */
    String coordToLc(int[] coord) {
        String lc = "";
        switch (coord[0]) {
            case 0:lc += "A";break;
            case 1:lc += "B";break;
            case 2:lc += "C";break;
            case 3:lc += "D";break;
            case 4:lc += "E";break;
            case 5:lc += "F";break;
            case 6:lc += "G";break;
            default:return "";
        }
        lc += "" + coord[1];
        return lc;
    }

    /**
     * Cherche le polygone cliqué
     *
     * @param coord coordonnées du clic
     * @return la case Lc du polygone cliqué
     */
    String findPoly(double[] coord) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (state[i][j] != '-' ){
                double distanceX = Math.pow((pointCentre[i][j][0] - coord[0]), 2);
                double distanceY = Math.pow((pointCentre[i][j][1] - coord[1]), 2);
                    if(distanceX + distanceY <= RAYON_POW2)
                        return coordToLc(new int[]{i, j});
                }
            }

        }
        return "";
    }

    /**
     * Calcule les coordonnées des sommets d'un polygone
     *
     * @param centre centre du polygone
     * @param size   taille du polygone
     * @return tableau de coordonnées des sommets du polygone
     */
    double[][] coordpoly(double[] centre, double size) {
        double[][] coordonnee = new double[2][6];
        for (int i = 0; i < 6; i++) {
            double angleRad = Math.PI / 180 * (60 * i);
            coordonnee[0][i] = centre[0] + (size * Math.cos(angleRad));
            coordonnee[1][i] = centre[1] + size * Math.sin(angleRad);
        }
        return coordonnee;
    }

    /**
     * Calcule les sommets des polygones
     *
     * 
     */
    void preShow() {
        StdDraw.setCanvasSize(800, 800);
        StdDraw.setXscale(0, 8);
        StdDraw.setYscale(0, 8);
        StdDraw.setTitle("STUCKWIN");
        StdDraw.enableDoubleBuffering();
        double[][][] tcenters = new double[7][8][2];
        double indiceH = 8.0;
        double indiceW = -2.0;
        double indiceTempH;
        double indiceTempW;
        for (int i = 0; i < BOARD_SIZE; i++) {
            indiceTempH = indiceH;
            indiceTempW = indiceW;
            for (int j = 0; j < SIZE; j++) {
                if (state[i][j] != '-') {
                    tcenters[i][j] = new double[]{indiceTempW * ((3.0 / 4.0) * WITDH), indiceTempH * 0.5 * HEIGHT};
                }
                indiceTempH += 1;
                indiceTempW += 1;
            }
            indiceH--;
            indiceW++;
        }
        pointCentre= tcenters;
    }

    /**
     * Affiche les pions sur le plateau
     *
     */
    void showPion() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (state[i][j] == 'R') {
                    StdDraw.setPenColor(new Color(219, 112, 147));
                    StdDraw.filledCircle(pointCentre[i][j][0], pointCentre[i][j][1], 0.35);
                    StdDraw.setPenColor(StdDraw.PINK);
                    StdDraw.filledCircle(pointCentre[i][j][0], pointCentre[i][j][1], 0.24);
                } else if (state[i][j] == 'B') {
                    StdDraw.setPenColor(new Color(70, 130, 180));
                    StdDraw.filledCircle(pointCentre[i][j][0], pointCentre[i][j][1], 0.35);
                    StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
                    StdDraw.filledCircle(pointCentre[i][j][0], pointCentre[i][j][1], 0.24);
                }
            }
        }
    }

    /**
     * Affiche la grille de jeu dans StdDraw
     *
     */
    void grid() {
        StdDraw.setPenColor(StdDraw.BLACK);
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (state[i][j] != '-') {
                    double[][] temp = coordpoly(pointCentre[i][j], SIZE_POLY);
                    StdDraw.polygon(temp[0], temp[1]);
                    StdDraw.text(pointCentre[i][j][0], pointCentre[i][j][1], coordToLc(new int[]{i, j}));
                }
            }
        }

    }

    /**
     * Affiche le plateau de jeu dans la configuration portée par
     * l'attribut d'état "state" sur StdDraw
     *
     * 
     */
    void afficheGUI() {
        StdDraw.pause(10);
        StdDraw.clear();
        showPion();
        grid();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(3.8, 7.5, "Tour des pions: ");
    }

   
   
    /**
     * Affiche le plateau de jeu dans la configuration portée par
     * l'attribut d'état "state" dans laffichage choisi
     */
    void affiche() {
        if (modeAffichage == Affichage.GUI) {
            afficheGUI();
        } else {
            afficheConsole();
        }  
    }


    /**
     * Affiche le plateau de jeu dans la configuration portée par
     * l'attribut d'état "state" dans la console
     */
    void afficheConsole(){
        String[] temp =
            {"      ", "    ", "  ", "", "  ", "", "  ", "", "  ", "", "  ", "    ", "      ", ""};
        char[] lettres = {'A', 'B', 'C', 'D', 'E', 'F', 'G'};
        int indice = 7;
        int indiceTemp;
        for (int i = 0; i < BOARD_SIZE; i++) {
            indiceTemp = indice;
            for (int j = 0; j < SIZE; j++) {
                if (state[i][j] == '.') {
                    temp[indiceTemp] += WHITE_BACKGROUND + lettres[i] + j + RESET + "  ";
                } else if (state[i][j] == 'R') {
                    temp[indiceTemp] += RED_BACKGROUND + lettres[i] + j + RESET + "  ";
                } else if (state[i][j] == 'B') {
                    temp[indiceTemp] += BLUE_BACKGROUND + lettres[i] + j + RESET + "  ";
                }
                indiceTemp--;
            }
            indice++;

        }
        for (int i = 0; i < temp.length; i++) {
            System.out.println(temp[i]);

        }
    }

    /**
     *  choisit une destination grace a l'IA choisie par l'utilisateur
     *
     * @param couleur couleur du pion à jouer
     * @return tableau contenant la position de départ et la destination du pion à jouer.
     */
    String[] jouerIA(char couleur) {
        char nextcouleur = (couleur == 'R') ? 'B' : 'R';
        if (modeIA == typeIA.RANDOM) {
            return randomIA(couleur);
        } else if (modeIA == typeIA.SCORE) {
            return scoreIA(couleur, nextcouleur);
        }
        return new String[]{"", ""}; 
    }

     /**
     * Choisit une destination aléatoire pour la couleur donnée
     *
     * @param couleur couleur du pion à jouer
     * @return tableau contenant la position de départ et la destination du pion à jouer.
     */
    String[] randomIA(char couleur) {
        while(true){
            int i =rand.nextInt(7);
            int j = rand.nextInt(8);
            if (state[i][j] == couleur) {
                String srcTemp = coordToLc(new int[]{i, j});
                for (String destTemp : possibleDests(couleur, i, j)) {
                    if (deplace(couleur, srcTemp, destTemp, ModeMvt.SIMU) == Result.OK) {
                        return new String[]{srcTemp, destTemp};
                    }
                }
            }
        }
    }


    /**
     *  Evalu la destination du pion
     * @param couleur couleur du pion à jouer
     * @param nextcouleur couleur du pion adverse
     * @param possibleDests tableau contenant les destinations possibles
     * @return score du mouvement
     */
    int evaluationDest(char couleur, char nextcouleur,String[] possibleDests){
        int score = 0;
        for(String destTemp : possibleDests){
            int[] destTempCoord = lcToCoord(destTemp);
            if(destTempCoord.length != 0){
            if(state[destTempCoord[0]][destTempCoord[1]] == nextcouleur) // Vérifier si le mouvement bloque un pion adverse
                score -= 100;
            // Vérifier si le mouvement se bloque differente maniere
            if(state[destTempCoord[0]][destTempCoord[1]] == couleur)
                score -= 20;
            else if(state[destTempCoord[0]][destTempCoord[1]] == '.')
                score -= 20;
            else if (state[destTempCoord[0]][destTempCoord[1]] == '-')
                score += 30;
            }
        }


        return score;
    }

    /**
     *  Evalu si le mouvement se bloque differente maniere
     * @param couleur couleur du pion à jouer
     * @param nextcouleur  couleur du pion adverse
     * @param possibleSrcs liste des mouvements possibles
     * @return score
     */
    int evaluationSrc(char couleur, char nextcouleur ,String[] possibleSrcs){
        int score = 0;
        for(String srcTemp : possibleSrcs){
            int[] srcTempCoord = lcToCoord(srcTemp);
            if(srcTempCoord.length != 0){
                if( state[srcTempCoord[0]][srcTempCoord[1]] == nextcouleur)
                    score += 60;
                else if (state[srcTempCoord[0]][srcTempCoord[1]] == '-' || state[srcTempCoord[0]][srcTempCoord[1]] == '.')
                    score += 10;
                else if (state[srcTempCoord[0]][srcTempCoord[1]] == couleur)
                    score += 50;
            }
            
        }

        return score;
    }



    /**
     * Calcule le score d'un mouvement
     *
     * @param couleur couleur du pion à jouer
     * @param mvt     tableau contenant la position de départ et la destination du pion à jouer.
     * @return score du mouvement
     */
    int scoreMvt(char couleur , char nextcouleur , String[] mvt){
        int score = 0;
        Result resultat = deplace(couleur,mvt[0],mvt[1],ModeMvt.SIMU);
        if(resultat != Result.OK)
            return -9999;
        
        
        int[] src = lcToCoord(mvt[0]);
        int[] dest = lcToCoord(mvt[1]);
        String[] possibleDests = possibleDests(couleur,dest[0],dest[1]);

        score += evaluationDest(couleur,nextcouleur,possibleDests);
        
        // Vérifier si le mouvement debloque un pion

        String[] possibleSrcs = possibleDests(couleur,src[0],src[1]);
        possibleSrcs = removeElement(possibleSrcs,mvt[1]);
        score += evaluationSrc(couleur,nextcouleur,possibleSrcs);

        // Vérifier si le mouvement debloque un de ses pion   
        String[] possiblSrcsBack = possibleDests(nextcouleur,src[0],src[1]);
        for (String srcTemp : possiblSrcsBack){
            int[] srcTempCoord = lcToCoord(srcTemp);
            if(srcTempCoord.length != 0 && state[srcTempCoord[0]][srcTempCoord[1]] == couleur)
                score -= 10;
            
        }
        return score;
    }


    /**
     *  Supprime un element d'un tableau de String
     * @param tab tableau de String
     * @param element element à supprimer
     * @return tableau de String sans l'element
     */
    String[] removeElement(String[] tab, String element){
        String[] newTab = new String[tab.length-1];
        int i = 0;
        for(String elementTemp : tab){
            if(!elementTemp.equals(element)){
                newTab[i] = elementTemp;
                i++;
            }
        }
        return newTab;
    }

  

    /**
     * Joue un tour en fonction du score du mouvement
     *
     * @param couleur couleur du pion à jouer
     * @return tableau contenant la position de départ et la destination du pion à jouer.
     */
    String[] scoreIA(char couleur ,char nextcouleur) {
        String[] mvt = new String[]{"", ""};
        int score = -9999999;
        int scoreTemp;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (state[i][j] == couleur) {
                    String srcTemp = coordToLc(new int[]{i, j});
                    for (String destTemp : possibleDests(couleur, i, j)) {
                        scoreTemp = scoreMvt(couleur, nextcouleur , new String[]{srcTemp, destTemp});
                        if (scoreTemp > score) {
                            score = scoreTemp;
                            mvt[0] = srcTemp;
                            mvt[1] = destTemp;
                        }
                    }
                }
            }
        }
        return mvt;

    }

   

    /**
     * gère le jeu en fonction du joueur/couleur
     *
     * @param couleur
     * @return tableau de deux chaînes {source,destination} du pion à jouer
     */
    String[] jouer(char couleur) {
        String src = "";
        String dst = "";
        String[] mvtIa;
        switch (couleur) {
            case 'B':
                System.out.println("Mouvement " + couleur);
                src = input.next().toUpperCase();
                dst = input.next().toUpperCase();
                System.out.println(src + "->" + dst);
                break;
            case 'R':
                
                System.out.println("Mouvement " + couleur);
                if(modeIA != typeIA.HUMAIN){
                mvtIa = jouerIA(couleur);
                src = mvtIa[0];
                dst = mvtIa[1];}
                else{
                    src = input.next().toUpperCase();
                    dst = input.next().toUpperCase();
                }
                System.out.println(src + "->" + dst);
                break;
            default:
                System.out.println("Couleur non reconnue");
                break;
        }
        return new String[]{src, dst};
    }

    /**
     * Detecte le clic de la souris et renvoie la position du polygone cliqué
     *
     * @return position du polygone cliqué
     */
    String clicMouse() {
        double[] cMouse = new double[2];
        String src = "";
        while ("".equals(src)) {
            if (StdDraw.isMousePressed()) {
                cMouse[0] = StdDraw.mouseX();
                cMouse[1] = StdDraw.mouseY();
                src = findPoly(cMouse);
            }
        }
        while (StdDraw.isMousePressed()) {
            StdDraw.pause(10);
        }
        return src;

    }

    /**
     * Affiche les possibilités de déplacement d'un pion
     *
     * @param couleur couleur du pion à jouer
     * @param src     position du pion séléctionné
     */
    void showPossibility(char couleur, String src) {
        int[] coordSrc = lcToCoord(src);
        int i = coordSrc[0];
        int j = coordSrc[1];
        if (state[i][j] == couleur) {
            for (String dest : possibleDests(couleur, i, j)) {
                if (deplace(couleur, src, dest, ModeMvt.SIMU) == Result.OK) {
                    int[] coordDest = lcToCoord(dest);
                    int x = coordDest[0];
                    int y = coordDest[1];
                    double[][] temp = coordpoly(pointCentre[x][y], SIZE_POLY * 0.8);
                    StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
                    StdDraw.setPenRadius(0.005);
                    StdDraw.polygon(temp[0], temp[1]);
                    StdDraw.setPenRadius();
                }
            }
        }
        StdDraw.show();
    }

    /**
     * Joue un tour en fonction du joueur/couleur
     *
     * @param couleur     couleur du pion à jouer
     * @return tableau de deux chaînes {source,destination} du pion à jouer
     */
    String[] jouerGUI(char couleur) {
        String src = "";
        String dst = "";
        String mouv = "Mouvement ";
        String[] mvtIa;
        switch (couleur) {
            case 'B':
                src = clicMouse();
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.text(3.5, 0.5, src + " ->");
                showPossibility(couleur, src);
                dst = clicMouse();
                afficheGUI();
                textJoueur(couleur);
                StdDraw.show();
                break;
            case 'R':
                if(modeIA!= typeIA.HUMAIN){
                mvtIa = jouerIA(couleur);
                src = mvtIa[0];
                dst = mvtIa[1];
                System.out.println(src + "->" + dst);
                }else{
                    System.out.println(mouv + couleur);
                src = clicMouse();
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.text(3.5, 0.5, src + " ->");
                showPossibility(couleur, src);
                dst = clicMouse();
                afficheGUI();
                textJoueur(couleur);
                StdDraw.show();
                }
                
                break;
            default:
                return new String[]{src, dst};
        }
        return new String[]{src, dst};
    }







    /**
     * retourne 'R' ou 'B' si vainqueur, 'N' si partie pas finie
     *
     * @param couleur couleur du pion à jouer
     * @return 'R' ou 'B' si vainqueur, 'N' si partie pas finie
     */
    char finPartie(char couleur) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (state[i][j] == couleur) {
                    String srcTemp = coordToLc(new int[]{i, j});
                    for (String dest : possibleDests(couleur, i, j)) {
                        if (deplace(couleur, srcTemp, dest, ModeMvt.SIMU) == Result.OK) {
                            return 'N';
                        }

                    }
                }
            }

        }
        return couleur;
    }

    /**
     * Affiche le gagant de la partie
     *
     * @param couleur couleur du gagnant
     */
    void afficheGagnant(char couleur) {
        if(modeAffichage==Affichage.GUI){
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setFont(new Font("Arial", Font.BOLD, 40));
        StdDraw.text(4, 5, "Le gagnant est le joueur:");
        StdDraw.setPenColor(couleur == 'B' ? new Color(70, 130, 180) : new Color(219, 112, 147));
        StdDraw.setFont(new Font("Arial", Font.BOLD, 60));
        StdDraw.text(4, 4, couleur == 'R' ? "ROUGE" : "BLEU");
        StdDraw.show();}
        else if (modeAffichage==Affichage.CONSOLE){
            affiche();
        }
    }
    
    /**
     * Choisis le mode d'affichage
     *
     */
    void choixAffichage() {
        int temp =0;
        do {
            System.out.println("Choisissez le mode d'affichage:");
            System.out.println("0 - Console");
            System.out.println("1 - Graphique");
            try {
                temp = input.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Veuillez entrer un nombre");
                input.next();
            }
        } while (temp != 0 && temp != 1);
        modeAffichage = Affichage.values()[temp];
        if (temp ==1) preShow();
    }


     /**
     * Choisis l'adversaire 
     *
     */
    void choixAdversaire(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("1")) {
                modeIA = typeIA.SCORE;
                return;
            }else if (args[0].equals("2")) {
                modeIA = typeIA.RANDOM;
                return;
            }
        }
        
        modeIA = typeIA.HUMAIN;
        }
    /**
     * Affiche le joueur courant dans le GUI
     *
     * @param curCouleur couleur du joueur courant
     */
    void textJoueur(char curCouleur) {
        StdDraw.setPenColor(curCouleur == 'R' ? new Color(219, 112, 147) : new Color(70, 130, 180));
        StdDraw.text(3.8, 7.3, "R".equals(curCouleur+"") ? "Rouge" : "Bleu");
    }



    public static void main(String[] args) {
        StuckWin jeu = new StuckWin();
        String src = "";
        String dest = "";
        String[] reponse;
        Result status;
        char partie = 'N';
        char curCouleur = jeu.joueurs[0];
        char nextCouleur = jeu.joueurs[1];
        char tmp;
        int cpt = 0;
        System.out.println("Bienvenue dans le jeu StuckWin");
        jeu.choixAffichage();
        jeu.choixAdversaire(args);
            // version console et graphique
            do {
                // séquence pour Bleu ou rouge
                jeu.affiche();
                if (jeu.modeAffichage == Affichage.GUI) {
                    jeu.textJoueur(curCouleur);
                    StdDraw.show();
                }
                do {
                    status = Result.EXIT;
                    reponse = jeu.modeAffichage == Affichage.CONSOLE ? jeu.jouer(curCouleur) : jeu.jouerGUI(curCouleur);
                    src = reponse[0];
                    dest = reponse[1];
                    if ("q".equalsIgnoreCase(src))
                        return;
                    status = jeu.deplace(curCouleur, src, dest, ModeMvt.REAL);
                    if (status == Result.OK)
                        partie = jeu.finPartie(nextCouleur);
                    System.out.println("status : " + status + " partie : " + partie);
                } while (status != Result.OK && partie == 'N');
                tmp = curCouleur;
                curCouleur = nextCouleur;
                nextCouleur = tmp;
                cpt++;
            } while (partie == 'N');
           
            jeu.afficheGagnant(curCouleur);
            System.out.printf("Victoire : " + partie + " (" + (cpt / 2) + " coups)");
            System.out.println("\nFin du jeu");
    }
    
    

        
} 


