package com.example.model;

import com.framework.annotations.CDBField;
import com.framework.annotations.CDBPrimaryKey;
import com.framework.annotations.CDBRelation;
import com.framework.annotations.CDBTable;
import com.framework.orm.CDBBaseModel;


/**
 *
 * @author schvarcz
 */
@CDBTable(TableName = "Exemplo")
public class ExemploTabela extends CDBBaseModel<ExemploTabela>
{
	//TODO: Descobrir uma maneira de deixar isso como protected e/ou private.
    @CDBPrimaryKey
    @CDBField(fieldName="id_exemplo")
    public Integer idExemploTabela;

    @CDBField(fieldName = "nome_qualquer")
    public String nomeQualquer;
    
    @CDBField(fieldName = "id_tipo_tabela")
    public Integer idTipoTabela;
    
    @CDBRelation(relation = CDBRelation.DBRelations.BELONGS_TO, field = "id_tipo_tabela")
    public TipoTabela tipoTabela = null;

    public int getIdExemploTabela()
    {
        return idExemploTabela;
    }

    public String getNomeQualquer()
    {
        return nomeQualquer;
    }

    public int getIdTipoTabela()
    {
        return idTipoTabela;
    }

    public TipoTabela getTipoTabela()
    {
        if (tipoTabela == null)
        {
            //super.loadRelation("tipoTabela");
            tipoTabela = new TipoTabela();
            tipoTabela.findByPk(idTipoTabela);
        }
        return tipoTabela;
    }

    public void setIdExemploTabela(int idExemploTabela)
    {
        this.idExemploTabela = idExemploTabela;
    }

    public void setNomeQualquer(String nomeQualquer)
    {
        this.nomeQualquer = nomeQualquer;
    }

    public void setIdTipoTabela(int idTipoTabela)
    {
        this.idTipoTabela = idTipoTabela;
    }

    public void setTipoTabela(TipoTabela tipoTabela)
    {
        this.tipoTabela = tipoTabela;
    }
}
