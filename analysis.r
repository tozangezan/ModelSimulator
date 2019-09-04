r02 <- read.delim("./ModelSimulator/result_d_02.txt",sep='\t',header=FALSE)
r002 <- read.delim("./ModelSimulator/result_d_002.txt",sep='\t',header=FALSE)
r_rand <-read.delim("./ModelSimulator/result_rand_d02.txt",sep='\t',header=FALSE)
r_regul <- read.delim("./ModelSimulator/result_regular_d02.txt",sep='\t',header=FALSE)
r_rand_thres02 <-read.delim("./ModelSimulator/result_rand_d02_thres02.txt",sep='\t',header=FALSE)


r02_a <- r02[r02$V2==50,]
r002_a <- r002[r002$V2==50,]
r_rand_a <- r_rand[r_rand$V2==50,]
r_regul_a <- r_regul[r_regul$V2==50,]
r_rand_thres02_a <- r_rand_thres02[r_rand_thres02$V2==50,]

plot(r02_a$V1,r02_a$V4,col="blue",ylim=c(0,6000000))
points(r002_a$V1,r002_a$V4,col="red")
points(r_rand_a$V1,r_rand_a$V4,col="green")
points(r_regul_a$V1,r_regul_a$V4,col="black")
points(r_rand_thres02_a$V1,r_rand_thres02_a$V4,col="purple")


r02_b <- r02[r02$V1==110,]
r002_b <- r002[r002$V1==110,]
r_rand_b <- r_rand[r_rand$V1==110,]
r_regul_b <- r_regul[r_regul$V1==110,]
r_rand_thres02_b <- r_rand_thres02[r_rand_thres02$V1==110,]


plot(r02_b$V2,r02_b$V5,col="blue",ylim=c(0,6000000))
points(r002_b$V2,r002_b$V5,col="red")
points(r_rand_b$V2,r_rand_b$V5,col="green")
points(r_regul_b$V2,r_regul_b$V5,col="black")
points(r_rand_thres02_b$V2,r_rand_thres02_b$V5,col="purple")

