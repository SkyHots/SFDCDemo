package com.blank.demo.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TcRecord {

    int result;
    ArrayList<PlayRecord> play_record_list;

    public TcRecord(ArrayList<PlayRecord> play_record_list) {
        this.play_record_list = play_record_list;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public ArrayList<PlayRecord> getPlay_record_list() {
        return play_record_list;
    }

    public List<Float> getTemperature() {
        return play_record_list.stream().map(PlayRecord::getTemperature).collect(Collectors.toList());
    }

    public List<Float> getTemperatureValid(short index, int scope) {
        Stream<PlayRecord> playRecordStream = play_record_list.stream().filter(new Predicate<PlayRecord>() {
            @Override
            public boolean test(PlayRecord playRecord) {
                return playRecord.getWave_index() == index;
            }
        });
        List<Float> filteredList = IntStream.range(0, play_record_list.size())
                .filter(i -> (i + 1) % scope == 0)
                .mapToObj(i -> play_record_list.get(i))
                .map(PlayRecord::getTemperature)
                .collect(Collectors.toList());
        return filteredList;
    }

    public List<Float> getBemf() {
        return play_record_list.stream().map(PlayRecord::getRelative_bemf).collect(Collectors.toList());
    }

    public List<Float> getF0() {
        return play_record_list.stream().map(PlayRecord::getOutput_f0).collect(Collectors.toList());
    }

    public void setPlay_record_list(ArrayList<PlayRecord> play_record_list) {
        this.play_record_list = play_record_list;
    }
}
