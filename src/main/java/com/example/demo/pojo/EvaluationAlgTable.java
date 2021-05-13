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
public class EvaluationAlgTable extends Evaluation{
    private String spam;
    private String ham;
    private Integer totalNumSpam;
    private Integer totalNumHam;
    private Integer TN;
    private Integer FN;
    private Integer TP;
    private Integer FP;
}
