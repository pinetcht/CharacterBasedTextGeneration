# Character-Based Text Generation and Classification
## Features
* Text generation program emulating <b> Michael Scott, Luke Skywalker, and Phoebe Buffay </b>
* Built using bigram model with discount, achieving <b>66% accuracy and 8.3 perplexity</b>
* Naive Bayes classifier for labeling generated sentences with their respective characters

## Example Generated Sentences
### Phoebe Buffay
<div>
  <img src="phoebe-phoebe-buffay.gif" alt="phoebe buffay" width="200"/>
</div>

* thank you ' d be great but i got . it ?
* i wanted to read the purses ! good bye !
  
### Luke Skywalker
<div>
  <img src="star-wars.gif" alt="luke skywalker" width="200"/>
</div>

* i feel the force and will .
* vader my father . 

### Michael Scott
<div>
  <img src="michael-scott-steve-carell.gif" alt="michael scott" width="200"/>
</div>

* no no , i don ' t even the uh , hey . they embarrassed my my girlfriend
* yes yes , the thing . nothing here . whatever . another living . . one penny . . 

## Perplexity vs Discount Graph
<p align="center">
  <img src="Perplexity vs. Discount .png" alt="perplexity-discount graph" width="60%"/></br>
  <i> Lowest perplexity when discount is lowest - Generated text does not contain unknown words, so higher discount leads to higher perplexity</i>
</p>

## Credits
Developed by Grace Everts & Pine Netcharussaeng as fincal project for CS159 Natual Language Processing. Thank you to Dr. Dave Kauchak for advising us on this project!
