package com.anghel.investmenthelper.prediction.service.tribuo;
import com.anghel.investmenthelper.prediction.model.internal.TrainingRow;
import org.tribuo.MutableDataset;
import org.tribuo.classification.Label;

import java.util.List;

public interface TribuoDatasetService {


    MutableDataset<Label> buildDataset(List<TrainingRow> trainingRows);
}
