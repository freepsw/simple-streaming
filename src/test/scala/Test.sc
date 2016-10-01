1+1

def makeList(str: String*) ={
  if(str.isEmpty)
    Nil
  else
    str.toList
}

val list = makeList()