package com.example.demo.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName EvaluationDatasetMapper
 * @Description TODO  处理不同数据集的POJO
 * @Author 21971
 * @Date 2021/5/11 16:29
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationDataset {
    @MppMultiId
    @TableField(value = "id")
    private String id;  //数据集名称
    @MppMultiId
    @TableField(value = "algorithm")
    private String algorithm;
    private Float accuracy;
    private Float pre;
    private Float recall;
    private Float f1;
}
