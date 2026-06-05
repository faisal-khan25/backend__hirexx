package com.hirex.dto;

import java.util.List;

public class AtsResultDto {

    private int atsScore;
    private List<String> matchedKeywords;
    private List<String> missingKeywords;
    private List<String> suggestions;

    public AtsResultDto() {}

    public AtsResultDto(int atsScore,
                        List<String> matchedKeywords,
                        List<String> missingKeywords,
                        List<String> suggestions) {
        this.atsScore        = atsScore;
        this.matchedKeywords = matchedKeywords;
        this.missingKeywords = missingKeywords;
        this.suggestions     = suggestions;
    }

    public int getAtsScore() { return atsScore; }
    public void setAtsScore(int atsScore) { this.atsScore = atsScore; }

    public List<String> getMatchedKeywords() { return matchedKeywords; }
    public void setMatchedKeywords(List<String> matchedKeywords) { this.matchedKeywords = matchedKeywords; }

    public List<String> getMissingKeywords() { return missingKeywords; }
    public void setMissingKeywords(List<String> missingKeywords) { this.missingKeywords = missingKeywords; }

    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
}
