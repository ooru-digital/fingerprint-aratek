package com.aratek.trustfinger.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by hecl on 2018/9/22.
 */

public class FingerData implements Serializable {

    public Set<String> getFingerPositions() {
        return fingerPositions;
    }

    public List<byte[]> getTemplates() {
        return templates;
    }

    public boolean addFingerIndex(String fingerIndex) {
        return fingerPositions.add(fingerIndex);
    }

    public boolean addTemplate(byte[] template) {
        return templates.add(template);
    }

    public void clearFingerPositions() {
        fingerPositions.clear();
    }

    public void clearTemplates() {
        templates.clear();
    }

    private Set<String> fingerPositions = new HashSet<String>();
    private List<byte[]> templates = new ArrayList<byte[]>();
    //left little finger
    private byte[] left_little_image_1;
    private int left_little_quality_1;
    private byte[] left_little_image_2;
    private int left_little_quality_2;
    private byte[] left_little_image_3;
    private int left_little_quality_3;
    private byte[] left_little_template;
    //left ring finger
    private byte[] left_ring_image_1;
    private int left_ring_quality_1;
    private byte[] left_ring_image_2;
    private int left_ring_quality_2;
    private byte[] left_ring_image_3;
    private int left_ring_quality_3;
    private byte[] left_ring_template;
    //left middle finger
    private byte[] left_middle_image_1;
    private int left_middle_quality_1;
    private byte[] left_middle_image_2;
    private int left_middle_quality_2;
    private byte[] left_middle_image_3;
    private int left_middle_quality_3;
    private byte[] left_middle_template;
    //left index finger
    private byte[] left_index_image_1;
    private int left_index_quality_1;
    private byte[] left_index_image_2;
    private int left_index_quality_2;
    private byte[] left_index_image_3;
    private int left_index_quality_3;
    private byte[] left_index_template;
    //left thumb
    private byte[] left_thumb_image_1;
    private int left_thumb_quality_1;
    private byte[] left_thumb_image_2;
    private int left_thumb_quality_2;
    private byte[] left_thumb_image_3;
    private int left_thumb_quality_3;
    private byte[] left_thumb_template;

    //right little finger
    private byte[] right_little_image_1;
    private int right_little_quality_1;
    private byte[] right_little_image_2;
    private int right_little_quality_2;
    private byte[] right_little_image_3;
    private int right_little_quality_3;
    private byte[] right_little_template;
    //right ring finger
    private byte[] right_ring_image_1;
    private int right_ring_quality_1;
    private byte[] right_ring_image_2;
    private int right_ring_quality_2;
    private byte[] right_ring_image_3;
    private int right_ring_quality_3;
    private byte[] right_ring_template;
    //right middle finger
    private byte[] right_middle_image_1;
    private int right_middle_quality_1;
    private byte[] right_middle_image_2;
    private int right_middle_quality_2;
    private byte[] right_middle_image_3;
    private int right_middle_quality_3;
    private byte[] right_middle_template;
    //right index finger
    private byte[] right_index_image_1;
    private int right_index_quality_1;
    private byte[] right_index_image_2;
    private int right_index_quality_2;
    private byte[] right_index_image_3;
    private int right_index_quality_3;
    private byte[] right_index_template;
    //right thumb
    private byte[] right_thumb_image_1;
    private int right_thumb_quality_1;
    private byte[] right_thumb_image_2;
    private int right_thumb_quality_2;
    private byte[] right_thumb_image_3;
    private int right_thumb_quality_3;
    private byte[] right_thumb_template;

    public byte[] getUnkown_position_template() {
        return unkown_position_template;
    }

    public void setUnkown_position_template(byte[] unkown_position_template) {
        this.unkown_position_template = unkown_position_template;
    }

    private byte[] unkown_position_template;

    public FingerData() {
    }

    public byte[] getLeft_little_image_1() {
        return left_little_image_1;
    }

    public void setLeft_little_image_1(byte[] left_little_image_1) {
        this.left_little_image_1 = left_little_image_1;
    }

    public int getLeft_little_quality_1() {
        return left_little_quality_1;
    }

    public void setLeft_little_quality_1(int left_little_quality_1) {
        this.left_little_quality_1 = left_little_quality_1;
    }

    public byte[] getLeft_little_image_2() {
        return left_little_image_2;
    }

    public void setLeft_little_image_2(byte[] left_little_image_2) {
        this.left_little_image_2 = left_little_image_2;
    }

    public int getLeft_little_quality_2() {
        return left_little_quality_2;
    }

    public void setLeft_little_quality_2(int left_little_quality_2) {
        this.left_little_quality_2 = left_little_quality_2;
    }

    public byte[] getLeft_little_image_3() {
        return left_little_image_3;
    }

    public void setLeft_little_image_3(byte[] left_little_image_3) {
        this.left_little_image_3 = left_little_image_3;
    }

    public int getLeft_little_quality_3() {
        return left_little_quality_3;
    }

    public void setLeft_little_quality_3(int left_little_quality_3) {
        this.left_little_quality_3 = left_little_quality_3;
    }

    public byte[] getLeft_little_template() {
        return left_little_template;
    }

    public void setLeft_little_template(byte[] left_little_template) {
        this.left_little_template = left_little_template;
    }

    public byte[] getLeft_ring_image_1() {
        return left_ring_image_1;
    }

    public void setLeft_ring_image_1(byte[] left_ring_image_1) {
        this.left_ring_image_1 = left_ring_image_1;
    }

    public int getLeft_ring_quality_1() {
        return left_ring_quality_1;
    }

    public void setLeft_ring_quality_1(int left_ring_quality_1) {
        this.left_ring_quality_1 = left_ring_quality_1;
    }

    public byte[] getLeft_ring_image_2() {
        return left_ring_image_2;
    }

    public void setLeft_ring_image_2(byte[] left_ring_image_2) {
        this.left_ring_image_2 = left_ring_image_2;
    }

    public int getLeft_ring_quality_2() {
        return left_ring_quality_2;
    }

    public void setLeft_ring_quality_2(int left_ring_quality_2) {
        this.left_ring_quality_2 = left_ring_quality_2;
    }

    public byte[] getLeft_ring_image_3() {
        return left_ring_image_3;
    }

    public void setLeft_ring_image_3(byte[] left_ring_image_3) {
        this.left_ring_image_3 = left_ring_image_3;
    }

    public int getLeft_ring_quality_3() {
        return left_ring_quality_3;
    }

    public void setLeft_ring_quality_3(int left_ring_quality_3) {
        this.left_ring_quality_3 = left_ring_quality_3;
    }

    public byte[] getLeft_ring_template() {
        return left_ring_template;
    }

    public void setLeft_ring_template(byte[] left_ring_template) {
        this.left_ring_template = left_ring_template;
    }

    public byte[] getLeft_middle_image_1() {
        return left_middle_image_1;
    }

    public void setLeft_middle_image_1(byte[] left_middle_image_1) {
        this.left_middle_image_1 = left_middle_image_1;
    }

    public int getLeft_middle_quality_1() {
        return left_middle_quality_1;
    }

    public void setLeft_middle_quality_1(int left_middle_quality_1) {
        this.left_middle_quality_1 = left_middle_quality_1;
    }

    public byte[] getLeft_middle_image_2() {
        return left_middle_image_2;
    }

    public void setLeft_middle_image_2(byte[] left_middle_image_2) {
        this.left_middle_image_2 = left_middle_image_2;
    }

    public int getLeft_middle_quality_2() {
        return left_middle_quality_2;
    }

    public void setLeft_middle_quality_2(int left_middle_quality_2) {
        this.left_middle_quality_2 = left_middle_quality_2;
    }

    public byte[] getLeft_middle_image_3() {
        return left_middle_image_3;
    }

    public void setLeft_middle_image_3(byte[] left_middle_image_3) {
        this.left_middle_image_3 = left_middle_image_3;
    }

    public int getLeft_middle_quality_3() {
        return left_middle_quality_3;
    }

    public void setLeft_middle_quality_3(int left_middle_quality_3) {
        this.left_middle_quality_3 = left_middle_quality_3;
    }

    public byte[] getLeft_middle_template() {
        return left_middle_template;
    }

    public void setLeft_middle_template(byte[] left_middle_template) {
        this.left_middle_template = left_middle_template;
    }

    public byte[] getLeft_index_image_1() {
        return left_index_image_1;
    }

    public void setLeft_index_image_1(byte[] left_index_image_1) {
        this.left_index_image_1 = left_index_image_1;
    }

    public int getLeft_index_quality_1() {
        return left_index_quality_1;
    }

    public void setLeft_index_quality_1(int left_index_quality_1) {
        this.left_index_quality_1 = left_index_quality_1;
    }

    public byte[] getLeft_index_image_2() {
        return left_index_image_2;
    }

    public void setLeft_index_image_2(byte[] left_index_image_2) {
        this.left_index_image_2 = left_index_image_2;
    }

    public int getLeft_index_quality_2() {
        return left_index_quality_2;
    }

    public void setLeft_index_quality_2(int left_index_quality_2) {
        this.left_index_quality_2 = left_index_quality_2;
    }

    public byte[] getLeft_index_image_3() {
        return left_index_image_3;
    }

    public void setLeft_index_image_3(byte[] left_index_image_3) {
        this.left_index_image_3 = left_index_image_3;
    }

    public int getLeft_index_quality_3() {
        return left_index_quality_3;
    }

    public void setLeft_index_quality_3(int left_index_quality_3) {
        this.left_index_quality_3 = left_index_quality_3;
    }

    public byte[] getLeft_index_template() {
        return left_index_template;
    }

    public void setLeft_index_template(byte[] left_index_template) {
        this.left_index_template = left_index_template;
    }

    public byte[] getLeft_thumb_image_1() {
        return left_thumb_image_1;
    }

    public void setLeft_thumb_image_1(byte[] left_thumb_image_1) {
        this.left_thumb_image_1 = left_thumb_image_1;
    }

    public int getLeft_thumb_quality_1() {
        return left_thumb_quality_1;
    }

    public void setLeft_thumb_quality_1(int left_thumb_quality_1) {
        this.left_thumb_quality_1 = left_thumb_quality_1;
    }

    public byte[] getLeft_thumb_image_2() {
        return left_thumb_image_2;
    }

    public void setLeft_thumb_image_2(byte[] left_thumb_image_2) {
        this.left_thumb_image_2 = left_thumb_image_2;
    }

    public int getLeft_thumb_quality_2() {
        return left_thumb_quality_2;
    }

    public void setLeft_thumb_quality_2(int left_thumb_quality_2) {
        this.left_thumb_quality_2 = left_thumb_quality_2;
    }

    public byte[] getLeft_thumb_image_3() {
        return left_thumb_image_3;
    }

    public void setLeft_thumb_image_3(byte[] left_thumb_image_3) {
        this.left_thumb_image_3 = left_thumb_image_3;
    }

    public int getLeft_thumb_quality_3() {
        return left_thumb_quality_3;
    }

    public void setLeft_thumb_quality_3(int left_thumb_quality_3) {
        this.left_thumb_quality_3 = left_thumb_quality_3;
    }

    public byte[] getLeft_thumb_template() {
        return left_thumb_template;
    }

    public void setLeft_thumb_template(byte[] left_thumb_template) {
        this.left_thumb_template = left_thumb_template;
    }

    public byte[] getRight_little_image_1() {
        return right_little_image_1;
    }

    public void setRight_little_image_1(byte[] right_little_image_1) {
        this.right_little_image_1 = right_little_image_1;
    }

    public int getRight_little_quality_1() {
        return right_little_quality_1;
    }

    public void setRight_little_quality_1(int right_little_quality_1) {
        this.right_little_quality_1 = right_little_quality_1;
    }

    public byte[] getRight_little_image_2() {
        return right_little_image_2;
    }

    public void setRight_little_image_2(byte[] right_little_image_2) {
        this.right_little_image_2 = right_little_image_2;
    }

    public int getRight_little_quality_2() {
        return right_little_quality_2;
    }

    public void setRight_little_quality_2(int right_little_quality_2) {
        this.right_little_quality_2 = right_little_quality_2;
    }

    public byte[] getRight_little_image_3() {
        return right_little_image_3;
    }

    public void setRight_little_image_3(byte[] right_little_image_3) {
        this.right_little_image_3 = right_little_image_3;
    }

    public int getRight_little_quality_3() {
        return right_little_quality_3;
    }

    public void setRight_little_quality_3(int right_little_quality_3) {
        this.right_little_quality_3 = right_little_quality_3;
    }

    public byte[] getRight_little_template() {
        return right_little_template;
    }

    public void setRight_little_template(byte[] right_little_template) {
        this.right_little_template = right_little_template;
    }

    public byte[] getRight_ring_image_1() {
        return right_ring_image_1;
    }

    public void setRight_ring_image_1(byte[] right_ring_image_1) {
        this.right_ring_image_1 = right_ring_image_1;
    }

    public int getRight_ring_quality_1() {
        return right_ring_quality_1;
    }

    public void setRight_ring_quality_1(int right_ring_quality_1) {
        this.right_ring_quality_1 = right_ring_quality_1;
    }

    public byte[] getRight_ring_image_2() {
        return right_ring_image_2;
    }

    public void setRight_ring_image_2(byte[] right_ring_image_2) {
        this.right_ring_image_2 = right_ring_image_2;
    }

    public int getRight_ring_quality_2() {
        return right_ring_quality_2;
    }

    public void setRight_ring_quality_2(int right_ring_quality_2) {
        this.right_ring_quality_2 = right_ring_quality_2;
    }

    public byte[] getRight_ring_image_3() {
        return right_ring_image_3;
    }

    public void setRight_ring_image_3(byte[] right_ring_image_3) {
        this.right_ring_image_3 = right_ring_image_3;
    }

    public int getRight_ring_quality_3() {
        return right_ring_quality_3;
    }

    public void setRight_ring_quality_3(int right_ring_quality_3) {
        this.right_ring_quality_3 = right_ring_quality_3;
    }

    public byte[] getRight_ring_template() {
        return right_ring_template;
    }

    public void setRight_ring_template(byte[] right_ring_template) {
        this.right_ring_template = right_ring_template;
    }

    public byte[] getRight_middle_image_1() {
        return right_middle_image_1;
    }

    public void setRight_middle_image_1(byte[] right_middle_image_1) {
        this.right_middle_image_1 = right_middle_image_1;
    }

    public int getRight_middle_quality_1() {
        return right_middle_quality_1;
    }

    public void setRight_middle_quality_1(int right_middle_quality_1) {
        this.right_middle_quality_1 = right_middle_quality_1;
    }

    public byte[] getRight_middle_image_2() {
        return right_middle_image_2;
    }

    public void setRight_middle_image_2(byte[] right_middle_image_2) {
        this.right_middle_image_2 = right_middle_image_2;
    }

    public int getRight_middle_quality_2() {
        return right_middle_quality_2;
    }

    public void setRight_middle_quality_2(int right_middle_quality_2) {
        this.right_middle_quality_2 = right_middle_quality_2;
    }

    public byte[] getRight_middle_image_3() {
        return right_middle_image_3;
    }

    public void setRight_middle_image_3(byte[] right_middle_image_3) {
        this.right_middle_image_3 = right_middle_image_3;
    }

    public int getRight_middle_quality_3() {
        return right_middle_quality_3;
    }

    public void setRight_middle_quality_3(int right_middle_quality_3) {
        this.right_middle_quality_3 = right_middle_quality_3;
    }

    public byte[] getRight_middle_template() {
        return right_middle_template;
    }

    public void setRight_middle_template(byte[] right_middle_template) {
        this.right_middle_template = right_middle_template;
    }

    public byte[] getRight_index_image_1() {
        return right_index_image_1;
    }

    public void setRight_index_image_1(byte[] right_index_image_1) {
        this.right_index_image_1 = right_index_image_1;
    }

    public int getRight_index_quality_1() {
        return right_index_quality_1;
    }

    public void setRight_index_quality_1(int right_index_quality_1) {
        this.right_index_quality_1 = right_index_quality_1;
    }

    public byte[] getRight_index_image_2() {
        return right_index_image_2;
    }

    public void setRight_index_image_2(byte[] right_index_image_2) {
        this.right_index_image_2 = right_index_image_2;
    }

    public int getRight_index_quality_2() {
        return right_index_quality_2;
    }

    public void setRight_index_quality_2(int right_index_quality_2) {
        this.right_index_quality_2 = right_index_quality_2;
    }

    public byte[] getRight_index_image_3() {
        return right_index_image_3;
    }

    public void setRight_index_image_3(byte[] right_index_image_3) {
        this.right_index_image_3 = right_index_image_3;
    }

    public int getRight_index_quality_3() {
        return right_index_quality_3;
    }

    public void setRight_index_quality_3(int right_index_quality_3) {
        this.right_index_quality_3 = right_index_quality_3;
    }

    public byte[] getRight_index_template() {
        return right_index_template;
    }

    public void setRight_index_template(byte[] right_index_template) {
        this.right_index_template = right_index_template;
    }

    public byte[] getRight_thumb_image_1() {
        return right_thumb_image_1;
    }

    public void setRight_thumb_image_1(byte[] right_thumb_image_1) {
        this.right_thumb_image_1 = right_thumb_image_1;
    }

    public int getRight_thumb_quality_1() {
        return right_thumb_quality_1;
    }

    public void setRight_thumb_quality_1(int right_thumb_quality_1) {
        this.right_thumb_quality_1 = right_thumb_quality_1;
    }

    public byte[] getRight_thumb_image_2() {
        return right_thumb_image_2;
    }

    public void setRight_thumb_image_2(byte[] right_thumb_image_2) {
        this.right_thumb_image_2 = right_thumb_image_2;
    }

    public int getRight_thumb_quality_2() {
        return right_thumb_quality_2;
    }

    public void setRight_thumb_quality_2(int right_thumb_quality_2) {
        this.right_thumb_quality_2 = right_thumb_quality_2;
    }

    public byte[] getRight_thumb_image_3() {
        return right_thumb_image_3;
    }

    public void setRight_thumb_image_3(byte[] right_thumb_image_3) {
        this.right_thumb_image_3 = right_thumb_image_3;
    }

    public int getRight_thumb_quality_3() {
        return right_thumb_quality_3;
    }

    public void setRight_thumb_quality_3(int right_thumb_quality_3) {
        this.right_thumb_quality_3 = right_thumb_quality_3;
    }

    public byte[] getRight_thumb_template() {
        return right_thumb_template;
    }

    public void setRight_thumb_template(byte[] right_thumb_template) {
        this.right_thumb_template = right_thumb_template;
    }
}
