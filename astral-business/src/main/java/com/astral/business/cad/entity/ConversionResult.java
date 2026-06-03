package com.astral.business.cad.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * cad转换结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversionResult {

    private String conversionStatus;

    private Astral3DCad item;

    private String message;

}
