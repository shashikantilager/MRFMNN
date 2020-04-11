# MRFMNN- MapReduce based Fuzzy Min-Max Neural Network

## Abstract
Fuzzy Min-Max Neural Network (FMNN) is a pattern classification algorithm which incorporates fuzzy sets and neural network. It is most suitable for online algorithms. Based on this, a MapReduce-based Fuzzy Min-Max Neural Network (MRFMNN) algorithm for pattern classification is proposed using Twister framework. MapReduce approach is used for scaling up the FMNN for massive large scale datasets. We used standard membership, expansion and the contraction functions of the traditional FMNN algorithm. The performance of the MRFMNN is tested by using several benchmark and synthetic datasets against the traditional FMNN. Results empirically established that MRFMNN achieves significant computational gains over FMNN without compromising classification accuracy.

## Main Features of the Work
1. Novel parallel MapReduce based parallel algorithm for FMNN classfier.
2. Implementation of FMNN classifier. It has standard  sequential implementation and aslo  parallel MapReduce version based on the algorithm proposed in the paper.
3. The MapReduce version is implemneted using the Twister MapReduce framework.
4. Experiometns and performnace evaluation on the MapReduce cluster (please refer the paper below)


## References
1. Shashikant Ilager,  Prasad, P. S. V. S, [Scalable MapReduce-based Fuzzy Min-Max Neural Network for Pattern Classification. In Proceedings of the 18th ACM  International Conference on Distributed Computing and Networking (ICDCN 2017)]( https://dl.acm.org/citation.cfm?id=3007776) , Hyderabad, India, January 2017.
2.  J. Ekanayake, H. Li, B. Zhang, T. Gunarathne, S.-H.Bae, J. Qiu, and G. C. Fox. Twister:  a runtime foriterative mapreduce. In S. Hariri and K. Keahey,editors,HPDC, pages 810–818. ACM, 2010.
3. P. K. Simpson. Fuzzy min-max neural networks classification. IEEE Trans. Neural Networks,3(5):776–786, 1992.

