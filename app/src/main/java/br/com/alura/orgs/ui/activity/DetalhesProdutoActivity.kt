package br.com.alura.orgs.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import br.com.alura.orgs.R
import br.com.alura.orgs.database.AppDataBase
import br.com.alura.orgs.databinding.ActivityDetalhesProdutoBinding
import br.com.alura.orgs.extensions.formataParaMoedaBrasileira
import br.com.alura.orgs.extensions.tentaCarregarImagem
import br.com.alura.orgs.model.Produto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.collect

class DetalhesProdutoActivity : AppCompatActivity() {

    private var produtoId: Long = 0
    private var produto: Produto? = null
    private val binding by lazy {
        ActivityDetalhesProdutoBinding.inflate(layoutInflater)
    }


    private val produtoDao by lazy {
        AppDataBase.instancia(this).produtoDao()
    }

    private val scope = CoroutineScope(IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        tentaCarregarProduto()
    }

    override fun onResume() {
        super.onResume()
        buscaProduto()
    }

    private fun buscaProduto() {
        lifecycleScope.launch {
            produtoDao.buscaPorId(produtoId).collect{ produtoEncontrado ->
                produto = produtoEncontrado
                produto?.let{
                    preencheCampos(it)
                } ?:finish()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detalhes_produto, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_detalhes_produto_remover -> {
                lifecycleScope.launch {
                    produto?.let{
                        produtoDao.remove(it)
                        finish()
                    }
                }
            }
            R.id.menu_detalhes_produto_editar -> {
                Intent(this, FormularioProdutoActivity::class.java).apply {
                    putExtra(CHAVE_PRODUTO_ID, produtoId)
                    startActivity(this)
                }
            }


        }
    return super.onOptionsItemSelected(item)
}

private fun tentaCarregarProduto() {
    produtoId = intent.getLongExtra(CHAVE_PRODUTO_ID, 0L)
}

private fun preencheCampos(produtoCarregado: Produto) {
    with(binding) {
        activityDetalhesProdutoImagem.tentaCarregarImagem(produtoCarregado.imagem)
        activityDetalhesProdutoNome.text = produtoCarregado.nome
        activityDetalhesProdutoDescricao.text = produtoCarregado.descricao
        activityDetalhesProdutoValor.text =
            produtoCarregado.valor.formataParaMoedaBrasileira()
    }
}

}