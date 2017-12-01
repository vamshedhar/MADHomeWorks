package io.vamshedhar.searchwords;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 9/23/17 5:15 PM.
 * vchinta1@uncc.edu
 */

public class Util {

    public static ArrayList getDataFromFile(InputStream is) {

        ArrayList<String> fileLines = new ArrayList<>();

        BufferedReader inputStream = null;

        try {
            try {
                inputStream = new BufferedReader(new InputStreamReader(is));
                String lineContent;

                while ((lineContent = inputStream.readLine()) != null) {
                    fileLines.add(lineContent.replaceAll("\t", " "));
                }
            }
            finally {
                if (inputStream != null)
                    inputStream.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return fileLines;
    }


    public static ArrayList wordSearch(ArrayList<String> fileLines, String keyWord, boolean matchCases){
        if (!matchCases){
            keyWord = keyWord.toLowerCase();
        }

        ArrayList<SearchResult> lines = new ArrayList<>();
        for(int i = 0; i < fileLines.size(); i++){
            String line = fileLines.get(i).trim();

            String[] lineWords = line.split(" ");

            if(!matchCases) {
                lineWords = line.toLowerCase().split(" ");
            }

            ArrayList<String> wordsList = new ArrayList<>(Arrays.asList(lineWords));

            if(wordsList.indexOf(keyWord) > 0){

                int wordIndex = wordsList.indexOf(keyWord);
                int oldIndex = wordIndex;

                while(oldIndex >= 0){
                    int index = TextUtils.join(" ", Arrays.copyOfRange(lineWords, 0, wordIndex)).length();

                    oldIndex = (wordsList.subList(wordIndex + 1, wordsList.size())).indexOf(keyWord);

                    wordIndex = wordIndex + oldIndex + 1;
                    if(index != 0){
                        index = index + 1;
                    }

                    int beginIndex = index - 10;

                    String oldStartString = "";

                    boolean lastWord = false;
                    int lastStringBalance = 0;

                    if(i == fileLines.size() - 1){
                        int availableLength = line.length() - (index + keyWord.length());
                        int requiredLength = 20 - keyWord.length();
                        if(availableLength < requiredLength){
                            lastWord = true;
                            if(index >= requiredLength - availableLength + 10){
                                oldStartString = line.substring(index - requiredLength + availableLength - 10, index);
                            } else{
                                oldStartString = line.substring(0, index);
                                lastStringBalance = requiredLength - availableLength + 10 - oldStartString.length();
                            }
                        }

                    }

                    if(beginIndex < 0 || lastWord){
                        beginIndex = 0;
                        int currentIndex = i;
                        int balanceLength = 10 - index;

                        if(lastWord){
                            balanceLength = lastStringBalance;
                            beginIndex = index;
                        }

                        while(balanceLength > 0 && currentIndex - 1 >= 0){

                            String oldLine = fileLines.get(currentIndex - 1).trim();

                            if(oldLine.length() != 0){
                                oldLine = oldLine + " ";
                            }

                            if(oldLine.length() < balanceLength){
                                oldStartString = oldLine + oldStartString;
                                balanceLength = balanceLength - oldLine.length();
                            } else{
                                oldStartString =  oldLine.substring(oldLine.length() - balanceLength) + oldStartString;
                                balanceLength = 0;
                            }

                            currentIndex = currentIndex - 1;
                        }
                    }

                    String startString = oldStartString + line.substring(beginIndex, index);

                    int endIndex = index - startString.length() + 30;

                    String endString;

                    if(endIndex >= line.length()){
                        endString = line.substring(index + keyWord.length());
                    } else {
                        endString = line.substring(index + keyWord.length(), endIndex);
                    }

                    String finalString = startString + line.substring(index, index + keyWord.length()) + endString;

                    SearchResult searchResult = new SearchResult(new BigInteger(Integer.toString(i) + Integer.toString(10000 + index)), startString, line.substring(index, index + keyWord.length()), endString);

                    int currentIndex = i;

                    while(finalString.length() < 30 && currentIndex + 1 < fileLines.size()){

                        String nextLine = fileLines.get(currentIndex + 1).trim();

                        if(nextLine.length() != 0){
                            nextLine = " " + nextLine;
                        }

                        int balanceLength = 30 - finalString.length();

                        if(nextLine.length() < balanceLength){
                            finalString = finalString + nextLine;
                            searchResult.endString = searchResult.endString + nextLine;
                        } else{
                            finalString = finalString + nextLine.substring(0, balanceLength);
                            searchResult.endString = searchResult.endString + nextLine.substring(0, balanceLength);
                        }

                        currentIndex = currentIndex + 1;
                    }

                    lines.add(searchResult);
                }
            }
        }

        return lines;
    }

}
