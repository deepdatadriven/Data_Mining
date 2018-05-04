#Problem 1
#read the csv file
poll = read.csv("C:/NJIT Lin Tang/CS634/Final/presidential_polls.csv")
#Remove the "20" at the beginning of date if any
poll$createddate = gsub("^20","",as.character(poll$createddate))
#specify date format
poll$createddate = as.Date(poll$createddate, format = "%m/%d/%y")
#extract months
months = strftime(poll$createddate, "%Y-%m")
#compute average poll without missing values
adjpoll_clinton_month_avg = aggregate(poll$adjpoll_clinton ~ months, FUN = mean, na.rm = TRUE, data = poll)
adjpoll_trump_month_avg = aggregate(poll$adjpoll_trump ~ months, FUN = mean, na.rm = TRUE, data = poll)
adjpoll_johnson_month_avg = aggregate(poll$adjpoll_johnson ~ months, FUN = mean, na.rm = TRUE, data = poll)
#merge data into a melt data frame
a = data.frame(adjpoll_clinton_month_avg,"Clinton")
colnames(a) = c("month","poll","name")
b =  data.frame(adjpoll_trump_month_avg,"Trump")
colnames(b) = c("month","poll","name")
c = data.frame(adjpoll_johnson_month_avg,"Johnson")
colnames(c) = c("month","poll","name")
poll_month_avg = rbind(a,b,c)
poll_month_avg
#plot the three lines in one plot

library(ggplot2)
ggplot(poll_month_avg, aes(x = month, y = poll, group = name, color = name)) +
geom_line(size = 1.5)  + 
theme(axis.text.x = element_text(angle = 45)) + 
ggtitle("Trends of the polls over time (by month)") + 
labs(y = "Percentage of intended vote/ Adjusted poll\n") +
labs(x = "Month")



#Problem 2/3/4 
library(randomForest) 
poll$outcome = ifelse(poll$adjpoll_clinton > poll$adjpoll_trump, 1, 0)
poll$diffdays = as.Date(poll$enddate, format = "%m/%d/%y") - as.Date(poll$startdate, format = "%m/%d/%y")  
poll$collecteddays = poll$createddate - min(poll$createddate)
pollnum = na.omit(poll[,c(9,10,11,12,13,28,29,30)])
#M1 = randomForest(factor(outcome) ~  grade + samplesize + population + poll_wt + diffdays + collecteddays, data = pollnum)
#M2 = glm(factor(outcome) ~  grade + samplesize + population + poll_wt + diffdays + collecteddays, data = pollnum, family = binomial)

set.seed(2016)
nrows <- dim(pollnum)[1]
#randomize
pollVld <- pollnum[sample(1:nrows), ]
kfold <- 5

splitIndex <- (1:nrows)%%kfold
splitFactor <- factor(splitIndex[order(splitIndex)])

pollSub <- split(pollVld,splitFactor)
print(dim(pollSub[[1]]))

error1_vec <- error2_vec <- NULL
accuracy1_vec <- accuracy2_vec <- NULL
precision1_vec <- precision2_vec <- NULL
recall1_vec <- recall2_vec <- NULL
pred1 <- pred2 <- NULL

for(iValid in seq(1,kfold)) {
  trData <- NULL
  vaData <- NULL
  for(j in seq(1,kfold)) {
    if(j!=iValid){
      trData <- rbind(trData,pollSub[[j]])
    }
    else {
      vaData <- pollSub[[j]]
    }
  }

  m1Train = randomForest(factor(outcome) ~ grade + samplesize + population + poll_wt + diffdays + collecteddays, data = trData)
  m2Train = glm(factor(outcome) ~  grade + samplesize + population + poll_wt + diffdays + collecteddays, data = trData, family = binomial)

  #####Random forests Model
  tbm1 = table(predict(m1Train,vaData, type="class"), vaData$outcome)
  print(tbm1)
  tbm3 = as.matrix(tbm1)
  TP1 <- tbm3[1, 1]
  FP1 <- tbm3[2, 1]
  FN1 <- tbm3[1, 2]
  TN1 <- tbm3[2, 2]   
  P1 <- TP1 + FN1
  N1 <- FP1 + TN1 
  accuracy1 <- (TP1 + TN1) / (P1 + N1)
  error_rate1<- (FP1 + FN1) / (P1 + N1)
  precision1 <- TP1 / (TP1 + FP1)
  recall1 <- TP1 / P1
  error1_vec<-c(error1_vec,error_rate1)
  accuracy1_vec<-c(accuracy1_vec,accuracy1)
  precision1_vec<-c(precision1_vec,precision1)
  recall1_vec<-c(recall1_vec,recall1)
  pred1 = c(pred1, predict(m1Train,vaData, type="class"))
  
  #####Logistic regression Model
  tbm2 = table(ifelse(predict(m2Train,vaData, type="response") > 0.5,1,0), vaData$outcome)
  print(tbm2)
  tbm4 = as.matrix(tbm2)
  TP2 <- tbm4[1, 1]
  FP2 <- tbm4[2, 1]
  FN2 <- tbm4[1, 2]
  TN2 <- tbm4[2, 2]   
  P2 <- TP2 + FN2
  N2 <- FP2 + TN2 
  error_rate2 <- (FP2 + FN2) / (P2 + N2)
  accuracy2 <- (TP2 + TN2) / (P2 + N2)
  precision2 <- TP2 / (TP2 + FP2)
  recall2 <- TP2 / P2
  error2_vec<-c(error2_vec,error_rate2)
  accuracy2_vec<-c(accuracy2_vec,accuracy2)
  precision2_vec<-c(precision2_vec,precision2)
  recall2_vec<-c(recall2_vec,recall1)
  pred2 = c(pred2, ifelse(predict(m2Train,vaData, type="response") > 0.5,1,0))
}
predictm1 = ifelse(mean(pred1) > 0.5,1,0)
predictm2 = ifelse(mean(pred2) > 0.5,1,0)

#Result of 2/3/4
#####Random forests Model
#Error rate values of Random forests:
error1_vec
#Accuracy values of Random forests:
accuracy1_vec
#Precision values of Random forests: 
precision1_vec
#Recall values of Random forests:
recall1_vec

#####Logistic regression Model
#Error rate values of Logistic regression:
error2_vec
#Accuracy values of Logistic regression:
accuracy2_vec
#Precision values of Logistic regression: 
precision2_vec
#Recall values of Logistic regression:
recall2_vec


#Who is the winner of Random forests  1:Clinton wins, 0: Trump wins: 
predictm1
#Who is the winner of Logistic regression  1:Clinton wins, 0: Trump wins:
predictm2

print(paste('Precision of Random forests: ', mean(precision1_vec)))
print(paste('Precision of Logistic regression: ', mean(precision2_vec)))

avg_err1 <- mean(error1_vec)
avg_err2 <- mean(error2_vec)
difference <- error1_vec - error2_vec - (avg_err1 - avg_err2)
k <- length(precision1_vec)
var <- t(difference)%*%difference/k
t <- (avg_err1 - avg_err2)/sqrt(var/k)
print(paste('The t value of the two models is : ', t))