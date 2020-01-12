#Nutshell
Automatic text analyzer.  Extract the key context keywords and phrases from a text either as individual text or as part of a larger corpus. As there are many different approaches described in the literature, ensure is flexible and efficient to test different ones changing program arguments. Provide key modules for the underlying data-structure, text-analysis and visualization that are easy to reuse by other users, as there are not many good NLP libraries in java.

User provides a list of stopwords and a text file, program automatically extracts keywords it finds more relevant in the text file or extracts a list of key phrases to form an abstract.

Optionally the user can provide a full text corpus in which case the program aims to find relevance vs such corpus. 

Program outputs results with relative weight to the console in a way that program can be easily used as part of a more comprehensive analysis or project, this output only requires saving into a text file and can be exported to excel or matlab for example.

As an extra feature program may output a HTML file with a wordcloud visualization.

##Usage:

<pre>nutshell + command line arguments as follows: 
usage: nutshell -f &lt;source.txt&gt; -om|-os|-oa &lt;n&gt; (-c &lt;directory&gt;) (-v) (-sc
                &lt;option&gt;)
 -c &lt;arg&gt;      Optional: Corpus differential analysis vs all .txt files in
               the supplied dir
 -f &lt;arg&gt;      Source .txt file
 -h            Show this help
 -oa &lt;arg&gt;     Abstract output &lt;n&gt;
 -om &lt;arg&gt;     Muti-word keyword output &lt;n&gt;
 -os &lt;arg&gt;     Single-word keyword output &lt;n&gt;
 -sc &lt;arg&gt;     Optional: Scoring options:[DEGREE, WEIGHTED_DEGREE,
               ENTROPY, RELATIVE_DEGREE, FREQUENCY]
 -stop &lt;arg&gt;   Optional: Stopwords file (default is stopwords_EN.txt)
 -v            Optional: Create Visualization nutshell.html file
</pre>

###Nutshell has two key modes of operation:
1. Analyze a Single .txt File (DEFAULT)
2. Differential analysis vs a corpus of text (`-c <directory>`), program automatically scans all .txt files in the supplied directory. 

Each mode of operation may output either single word keywords (`-os <n>`), composite keywords with one or more words (`-om <n>`), or key phrases (`-oa <n>`) in which case delimiters are punctuation only and stopwords are included though do not add points to the phrase weight.

Modes om and os may be asked to also output a HTML file named `nutshell.html` with a word-cloud visualisation of the results by adding the `-v` argument, `-oa` also may output a visualization though at this point is experimental only.

###Stopwords:
Nutshell requires a text file with stopwords on any language, by default it searches for a file named `stopwords_EN.txt` though any txt file may be provided adding the option `-stop <filename>`

###Scoring options
Nutshell builds a weighed directed graph of word co-ocurrences.
* WEIGHTED_DEGREE (DEFAULT): considers each word degree multiplied by corresponding edge weight.
* DEGREE: Word degree.
* ENTROPY:  entropy = Sum(prob(w) x log(prob(w))) this scoring system is more meaningful on corpus scoring considering the probability of finding a word as the relative frequency of such word in the corpus.
* FREQUENCY: relative frequency of the word

When comparing a word vs the full corpus scoring of a word in the file under ananlysis is considering as relative vs the corpus, with exception to entropy which is considered additive.

###Dependencies
See `pom.xml` for Maven dependencies.