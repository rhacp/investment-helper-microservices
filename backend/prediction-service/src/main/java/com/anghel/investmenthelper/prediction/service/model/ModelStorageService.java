package com.anghel.investmenthelper.prediction.service.model;

import org.tribuo.Model;
import org.tribuo.classification.Label;

public interface ModelStorageService {

    String saveModel(String ticker, Model<Label> model, Integer version);

    Model<Label> loadModel(String modelPath);

    void deleteModel(String modelPath);
}
