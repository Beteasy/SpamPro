package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName Evaluation
 * @Description TODO
 * @Author 21971
 * @Date 2021/4/21 19:05
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evaluation {
    private String id;
    private Float accuracy;
    private Float pre;
    private Float recall;
    private Float f1;
}
