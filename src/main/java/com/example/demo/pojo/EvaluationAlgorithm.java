package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName EvaluationAlgTable
 * @Description TODO
 * @Author 21971
 * @Date 2021/5/12 18:04
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationAlgorithm extends Evaluation{
    private String spamTag;
    private String hamTag;
    private Integer totalNumSpam;
    private Integer totalNumHam;
    private Integer tn;
    private Integer fn;
    private Integer tp;
    private Integer fp;

    public String getSpamTag() {
        return spamTag;
    }

    public void setSpamTag(String spamTag) {
        this.spamTag = spamTag;
    }

    public String getHamTag() {
        return hamTag;
    }

    public void setHamTag(String hamTag) {
        this.hamTag = hamTag;
    }

    public Integer getTotalNumSpam() {
        return totalNumSpam;
    }

    public void setTotalNumSpam(Integer totalNumSpam) {
        this.totalNumSpam = totalNumSpam;
    }

    public Integer getTotalNumHam() {
        return totalNumHam;
    }

    public void setTotalNumHam(Integer totalNumHam) {
        this.totalNumHam = totalNumHam;
    }

    public Integer getTn() {
        return tn;
    }

    public void setTn(Integer tn) {
        this.tn = tn;
    }

    public Integer getFn() {
        return fn;
    }

    public void setFn(Integer fn) {
        this.fn = fn;
    }

    public Integer getTp() {
        return tp;
    }

    public void setTp(Integer tp) {
        this.tp = tp;
    }

    public Integer getFp() {
        return fp;
    }

    public void setFp(Integer fp) {
        this.fp = fp;
    }
}
