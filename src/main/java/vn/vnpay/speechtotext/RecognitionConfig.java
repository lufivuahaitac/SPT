/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.vnpay.speechtotext;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author truongnq
 */
@Getter
@Setter
@ToString
@Builder
public class RecognitionConfig {
    private boolean enableAutomaticPunctuation;
    private String encoding;
    private String model;
    private String languageCode;
    private Integer audioChannelCount;
    private boolean enableSeparateRecognitionPerChannel;
}
