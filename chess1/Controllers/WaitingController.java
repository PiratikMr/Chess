package org.example.chess1.Controllers;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class WaitingController implements Initializable {
    @FXML private Label tipsLabel;
    @FXML private ImageView imageOne, imageTwo;
    private final CircularLinkedList tips = new CircularLinkedList();
    private final CircularLinkedList images = new CircularLinkedList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imageOne.toBack();
        imageTwo.toBack();
        setTips();
        setImages();
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdateTips = 0, lastUpdateImage = 0;
            private final Random rand = new Random();
            private Node currTip = tips.get(rand.nextInt(tips.length));
            private Node currImage = images.get(rand.nextInt(images.length));
            private double from = 1.0, to = 0.0;
            @Override
            public void handle(long l) {
                if (lastUpdateImage == 0){
                    imageOne.setImage((Image) currImage.data);
                    currImage = currImage.next;
                    imageTwo.setImage((Image) currImage.data);
                    currImage = currImage.next;
                    lastUpdateImage = l;
                }
                if (l - lastUpdateTips >= 6_000_000_000L){
                    tipsLabel.setText((String) currTip.data);
                    currTip = currTip.next;
                    lastUpdateTips = l;
                }

                if (l - lastUpdateImage >= 10_000_000_000L){
                    FadeTransition fadeOut = new FadeTransition(Duration.seconds(2), imageOne);
                    fadeOut.setFromValue(from);
                    fadeOut.setToValue(to);

                    if (from > 0) {
                        fadeOut.setOnFinished(_->{
                            imageOne.setImage((Image) currImage.data);
                            currImage = currImage.next;
                        });
                    } else {
                        fadeOut.setOnFinished(_->{
                            imageTwo.setImage((Image) currImage.data);
                            currImage = currImage.next;
                        });
                    }

                    fadeOut.play();

                    double help = from;
                    from = to;
                    to = help;

                    lastUpdateImage = l;
                }
            }
        };
        timer.start();
    }

    //Add Functions
    private void setTips() {
        tips.add("Конь ходит буквой 'Г', не стоит этого забывать.");
        tips.add("Обычно, если объявляют шах, нужно как-то защитить короля.");
        tips.add("Тут должна быть подсказка.");
        tips.add("'Белые ходят первые, а черные потом' @Джейсон Стетхем.");
        tips.add("Джони, они на деревьях!");
        tips.add("Не стоит...");
        tips.add("'Шахматы - фигня полная, лучше геншин' @мальчик с ДЦП.");
        tips.add("'Ненавижу черных'@шахматный любитель.");
        tips.add("Для хода - нужно походить.");
        tips.add("Код этих шахмат был написан для закрытия РГЗ.");
        tips.add("Не забудь зарегистрироваться!");
        tips.add("Удачи!");
        tips.add("Ам Ам Ам Ам Ам Ам Ам.");
        tips.add("Число уникальных шахматных партий составляет 10^120.");
        tips.add("Теоретически, в самой длинной шахматной партии может быть 3 000 ходов.");
        tips.add("Шанс выпадения этой подсказки - " + (int)(1/(double)(tips.length+1)*100) + "%.");
    }

    private void setImages() {
        for (int i = 1; i < 4; i++) {
            images.add(new Image(getClass().getResourceAsStream("/org/example/chess1/WaitingScreens/image" + i + ".jpg")));
        }
    }

    private static class CircularLinkedList {
        private Node head;
        private Node tail;
        public int length = 0;

        public void add(Object data) {
            Node newNode = new Node(data);
            if (head == null) {
                head = newNode;
            } else {
                tail.next = newNode;
            }
            tail = newNode;
            tail.next = head;
            length++;
        }

        public Node get(int idx) {
            Node curr = head;
            for (int i=0;i<idx;i++){
                curr = curr.next;
            }
            return curr;
        }
    }
    private static class Node {
        Object data;
        Node next;

        public Node(Object data) {
            this.data = data;
        }
    }
}
